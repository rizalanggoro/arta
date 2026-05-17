package fxrate

import (
	"context"
	"log"

	gocron "github.com/go-co-op/gocron/v2"
)

// Scheduler periodically stores FX rates.
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
		s.logger.Println("fx rate cron already started")
		return
	}

	scheduler, err := gocron.NewScheduler()
	if err != nil {
		s.logger.Printf("fx rate scheduler init failed: %v", err)
		return
	}

	_, err = scheduler.NewJob(
		gocron.CronJob("*/10 * * * *", false),
		gocron.NewTask(func(jobCtx context.Context) {
			s.sync(jobCtx)
		}),
	)
	if err != nil {
		s.logger.Printf("fx rate job registration failed: %v", err)
		return
	}

	s.scheduler = scheduler
	s.scheduler.Start()
	s.logger.Println("fx rate cron started: */10 * * * *")

	go func() {
		<-ctx.Done()
		if err := s.scheduler.Shutdown(); err != nil {
			s.logger.Printf("fx rate scheduler shutdown failed: %v", err)
		}
	}()

	if ctx.Err() != nil {
		if err := s.scheduler.Shutdown(); err != nil {
			s.logger.Printf("fx rate scheduler shutdown failed: %v", err)
		}
	}
}

func (s *Scheduler) sync(ctx context.Context) {
	rate, err := s.client.FetchLatest(ctx)
	if err != nil {
		s.logger.Printf("fx rate sync failed: %v", err)
		return
	}

	created, err := s.repo.Create(rate)
	if err != nil {
		s.logger.Printf("fx rate save failed: %v", err)
		return
	}

	s.logger.Printf("fx rate synced: base=%s date=%s rate=%d", created.Base, created.Date.Format("2006-01-02"), created.Rate)
}
