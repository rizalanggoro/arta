package main

import (
	"context"
	"fmt"
	"log"
	"time"

	_ "github.com/artafinance/backend/docs"
	"github.com/artafinance/backend/internal/cron/fxrate"
	"github.com/artafinance/backend/internal/cron/goldprice"
	"github.com/artafinance/backend/internal/feature/auth"
	"github.com/artafinance/backend/internal/feature/category"
	"github.com/artafinance/backend/internal/feature/dashboard"
	"github.com/artafinance/backend/internal/feature/gold"
	"github.com/artafinance/backend/internal/feature/release"
	"github.com/artafinance/backend/internal/feature/transaction"
	"github.com/artafinance/backend/internal/feature/wallet"
	"github.com/artafinance/backend/pkg/config"
	"github.com/artafinance/backend/pkg/database"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/joho/godotenv"
	fiberSwagger "github.com/swaggo/fiber-swagger"
)

// @title ARTA API
// @version 1.0
// @description API untuk aplikasi manajemen keuangan dan pencatatan emas - ARTA
// @host localhost:8080
// @basePath /
// @schemes http https
// @securityDefinitions.apikey Bearer
// @in header
// @name Authorization
// @description "Type 'Bearer TOKEN'"

func main() {
	// Load environment variables
	if err := godotenv.Load(); err != nil {
		log.Println("Warning: .env file not found, using environment variables")
	}

	cfg := config.New()
	if location, err := time.LoadLocation(cfg.AppTimeZone); err != nil {
		log.Printf("Warning: failed to load timezone %q, using system local timezone: %v", cfg.AppTimeZone, err)
	} else {
		time.Local = location
	}

	db, err := database.Initialize(cfg)
	if err != nil {
		log.Fatal(err)
	}

	goldPriceRepo := goldprice.NewRepository(db)
	goldPriceClient := goldprice.NewClient()
	goldPriceJob := goldprice.NewScheduler(goldPriceRepo, goldPriceClient, log.Default())
	go goldPriceJob.Start(context.Background())

	fxRateRepo := fxrate.NewRepository(db)
	fxRateClient := fxrate.NewClient()
	fxRateJob := fxrate.NewScheduler(fxRateRepo, fxRateClient, log.Default())
	go fxRateJob.Start(context.Background())

	jwtManager := jwt.New(cfg.JWTSecret, cfg.JWTExpiration)
	authRepo := auth.NewRepository(db)
	authHandler := auth.NewHandler(authRepo, jwtManager)

	walletRepo := wallet.NewRepository(db)
	walletHandler := wallet.NewHandler(walletRepo, jwtManager, authRepo)

	categoryRepo := category.NewRepository(db)
	categoryHandler := category.NewHandler(categoryRepo, jwtManager, authRepo)

	goldRepo := gold.NewRepository(db, cfg)
	goldTaxRepo := gold.NewGoldTaxRepository(db, cfg)
	goldHandler := gold.NewHandler(
		goldRepo,
		fxRateRepo,
		goldPriceRepo,
		jwtManager,
		authRepo,
	)

	releaseRepo := release.NewRepository(db)
	releaseHandler := release.NewHandler(releaseRepo)

	transactionRepo := transaction.NewRepository(db)
	transactionHandler := transaction.NewHandler(transactionRepo, categoryRepo, jwtManager, authRepo)

	dashboardGoldRepo := dashboard.NewDashboardGoldRepository(db, cfg)
	dashboardHandler := dashboard.NewHandler(walletRepo, goldRepo, goldPriceRepo, fxRateRepo,
		transactionRepo, categoryRepo, jwtManager, authRepo, cfg, dashboardGoldRepo, goldTaxRepo)

	app := fiber.New()
	app.Use(logger.New())

	app.Get("/swagger/*", fiberSwagger.WrapHandler)

	api := app.Group("/api")
	authHandler.RegisterRoutes(api)
	walletHandler.RegisterRoutes(api)
	categoryHandler.RegisterRoutes(api)
	goldHandler.RegisterRoutes(api)
	releaseHandler.RegisterRoutes(api)
	transactionHandler.RegisterRoutes(api)
	dashboardHandler.RegisterRoutes(api)

	latestFxRate, err := fxRateRepo.GetLatest()
	if err != nil {
		log.Printf("Error fetching latest FX rate: %v", err)
	}
	latestGoldPrice, err := goldPriceRepo.GetLatest()
	if err != nil {
		log.Printf("Error fetching latest gold price: %v", err)
	}

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{
			"status":     "ok",
			"gold_price": latestGoldPrice,
			"fx_rate":    latestFxRate,
		})
	})

	fmt.Printf("ARTA Backend Server starting on port %s\n", cfg.ServerPort)
	if err := app.Listen(":" + cfg.ServerPort); err != nil {
		log.Fatal(err)
	}
}
