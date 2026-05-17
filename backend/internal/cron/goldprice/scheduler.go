package goldprice

import (
	"context"
	"log"

	gocron "github.com/go-co-op/gocron/v2"
)

// Scheduler periodically stores gold prices.
type Scheduler struct {
	repo      *Repository
	client    *Client
	logger    *log.Logger
	scheduler gocron.Scheduler
}

// NewScheduler creates a new scheduler.
func NewScheduler(repo *Repository, client *Client, logger *log.Logger) *Scheduler {
	if logger == nil {
		logger = log.Default()
	}
	return &Scheduler{
		repo:   repo,
		client: client,
		logger: logger,
	}
}

// Start runs the cron job and keeps it active until the provided context is canceled.
func (s *Scheduler) Start(ctx context.Context) {
	if s.scheduler != nil {
		s.logger.Println("gold price cron already started")
		return
	}

	scheduler, err := gocron.NewScheduler()
	if err != nil {
		s.logger.Printf("gold price scheduler init failed: %v", err)
		return
	}

	_, err = scheduler.NewJob(
		gocron.CronJob("*/10 * * * *", false),
		gocron.NewTask(func(jobCtx context.Context) {
			s.sync(jobCtx)
		}),
	)
	if err != nil {
		s.logger.Printf("gold price job registration failed: %v", err)
		return
	}

	s.scheduler = scheduler
	s.scheduler.Start()
	s.logger.Println("gold price cron started: */10 * * * *")

	go func() {
		<-ctx.Done()
		if err := s.scheduler.Shutdown(); err != nil {
			s.logger.Printf("gold price scheduler shutdown failed: %v", err)
		}
	}()

	if ctx.Err() != nil {
		if err := s.scheduler.Shutdown(); err != nil {
			s.logger.Printf("gold price scheduler shutdown failed: %v", err)
		}
	}
}

func (s *Scheduler) sync(ctx context.Context) {
	price, err := s.client.FetchLatest(ctx)
	if err != nil {
		s.logger.Printf("gold price sync failed: %v", err)
		return
	}

	created, err := s.repo.Create(price)
	if err != nil {
		s.logger.Printf("gold price save failed: %v", err)
		return
	}

	s.logger.Printf("gold price synced: %s %.6f %s (source updated %s)", created.Currency, created.PricePerOunceUSD, created.Symbol, created.SourceReadableAge)
}
