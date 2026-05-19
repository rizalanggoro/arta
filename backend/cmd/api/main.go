package main

import (
	"context"
	"fmt"
	"log"

	"github.com/artafinance/backend/internal/cron/fxrate"
	"github.com/artafinance/backend/internal/cron/goldprice"
	"github.com/artafinance/backend/internal/feature/auth"
	"github.com/artafinance/backend/internal/feature/category"
	"github.com/artafinance/backend/internal/feature/dashboard"
	"github.com/artafinance/backend/internal/feature/gold"
	"github.com/artafinance/backend/internal/feature/transaction"
	"github.com/artafinance/backend/internal/feature/wallet"
	"github.com/artafinance/backend/pkg/config"
	"github.com/artafinance/backend/pkg/database"
	"github.com/artafinance/backend/pkg/jwt"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/middleware/logger"
	"github.com/joho/godotenv"
)

// @title ARTA API
// @version 1.0
// @description API untuk aplikasi manajemen keuangan dan pencatatan emas - ARTA
// @host localhost:3000
// @basePath /api
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
	db, err := database.Initialize(cfg)
	if err != nil {
		log.Fatal(err)
	}

	jwtManager := jwt.New(cfg.JWTSecret, cfg.JWTExpiration)
	authRepo := auth.NewRepository(db)
	authHandler := auth.NewHandler(authRepo, jwtManager)

	walletRepo := wallet.NewRepository(db)
	walletHandler := wallet.NewHandler(walletRepo, jwtManager, authRepo)

	categoryRepo := category.NewRepository(db)
	categoryHandler := category.NewHandler(categoryRepo, jwtManager, authRepo)

	goldRepo := gold.NewRepository(db)

	transactionRepo := transaction.NewRepository(db)
	transactionHandler := transaction.NewHandler(transactionRepo, categoryRepo, jwtManager, authRepo)

	goldPriceRepo := goldprice.NewRepository(db)
	goldPriceClient := goldprice.NewClient()
	goldPriceJob := goldprice.NewScheduler(goldPriceRepo, goldPriceClient, log.Default())
	go goldPriceJob.Start(context.Background())

	fxRateRepo := fxrate.NewRepository(db)
	fxRateClient := fxrate.NewClient()
	fxRateJob := fxrate.NewScheduler(fxRateRepo, fxRateClient, log.Default())
	go fxRateJob.Start(context.Background())

	dashboardHandler := dashboard.NewHandler(walletRepo, goldRepo, goldPriceRepo, fxRateRepo, transactionRepo, categoryRepo, jwtManager, authRepo)

	app := fiber.New()
	app.Use(logger.New())
	api := app.Group("/api")
	authHandler.RegisterRoutes(api)
	walletHandler.RegisterRoutes(api)
	categoryHandler.RegisterRoutes(api)
	transactionHandler.RegisterRoutes(api)
	dashboardHandler.RegisterRoutes(api)

	app.Get("/health", func(c *fiber.Ctx) error {
		return c.JSON(fiber.Map{"status": "ok"})
	})

	fmt.Printf("ARTA Backend Server starting on port %s\n", cfg.ServerPort)
	if err := app.Listen(":" + cfg.ServerPort); err != nil {
		log.Fatal(err)
	}
}
