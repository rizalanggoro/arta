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
	"github.com/artafinance/backend/internal/feature/health"
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
	"go.uber.org/dig"
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
	// load environment variables
	if err := godotenv.Load(); err != nil {
		log.Println("Warning: .env file not found, using environment variables")
	}

	container := dig.New()

	// config
	must(container.Provide(config.New))
	must(container.Invoke(func(cfg *config.Config) {
		if location, err := time.LoadLocation(cfg.AppTimeZone); err != nil {
			log.Printf("Warning: failed to load timezone %q, using system local timezone: %v", cfg.AppTimeZone, err)
		} else {
			time.Local = location
		}
	}))

	// database
	must(container.Provide(database.Initialize))

	// utils
	must(container.Provide(jwt.New))

	// repositories
	must(container.Provide(goldprice.NewRepository))
	must(container.Provide(fxrate.NewRepository))
	must(container.Provide(auth.NewRepository))
	must(container.Provide(wallet.NewRepository))
	must(container.Provide(category.NewRepository))
	must(container.Provide(gold.NewRepository))
	must(container.Provide(gold.NewGoldTaxRepository))
	must(container.Provide(release.NewRepository))
	must(container.Provide(transaction.NewRepository))
	must(container.Provide(dashboard.NewDashboardGoldRepository))

	// handlers
	must(container.Provide(health.NewHandler))
	must(container.Provide(auth.NewHandler))
	must(container.Provide(wallet.NewHandler))
	must(container.Provide(category.NewHandler))
	must(container.Provide(gold.NewHandler))
	must(container.Provide(release.NewHandler))
	must(container.Provide(transaction.NewHandler))
	must(container.Provide(dashboard.NewHandler))

	// cron jobs
	must(container.Provide(log.Default))
	must(container.Provide(goldprice.NewClient))
	must(container.Provide(fxrate.NewClient))
	must(container.Provide(goldprice.NewScheduler))
	must(container.Provide(fxrate.NewScheduler))
	must(container.Invoke(func(
		job1 *goldprice.Scheduler,
		job2 *fxrate.Scheduler,
	) {
		go job1.Start(context.Background())
		go job2.Start(context.Background())
	}))

	// fiber app
	must(container.Provide(fiber.New))
	must(container.Invoke(func(
		app *fiber.App,
		cfg *config.Config,
		healthHandler *health.Handler,
		authHandler *auth.Handler,
		walletHandler *wallet.Handler,
		categoryHandler *category.Handler,
		goldHandler *gold.Handler,
		releaseHandler *release.Handler,
		transactionHandler *transaction.Handler,
		dashboardHandler *dashboard.Handler,
	) {
		app.Use(logger.New())
		app.Get("/swagger/*", fiberSwagger.WrapHandler)

		api := app.Group("/api")
		healthHandler.RegisterRoutes(api)
		authHandler.RegisterRoutes(api)
		walletHandler.RegisterRoutes(api)
		categoryHandler.RegisterRoutes(api)
		goldHandler.RegisterRoutes(api)
		releaseHandler.RegisterRoutes(api)
		transactionHandler.RegisterRoutes(api)
		dashboardHandler.RegisterRoutes(api)

		fmt.Printf("ARTA Backend Server starting on port %s\n", cfg.ServerPort)
		if err := app.Listen(":" + cfg.ServerPort); err != nil {
			log.Fatal(err)
		}
	}))
}

func must(err error) {
	if err != nil {
		panic(err)
	}
}
