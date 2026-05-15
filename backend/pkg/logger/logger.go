package logger

import (
	"log"
	"os"
)

// Logger provides logging functionality
type Logger struct {
	infoLog  *log.Logger
	warnLog  *log.Logger
	errorLog *log.Logger
}

// New creates a new Logger instance
func New() *Logger {
	return &Logger{
		infoLog:  log.New(os.Stdout, "[INFO] ", log.LstdFlags|log.Lshortfile),
		warnLog:  log.New(os.Stdout, "[WARN] ", log.LstdFlags|log.Lshortfile),
		errorLog: log.New(os.Stderr, "[ERROR] ", log.LstdFlags|log.Lshortfile),
	}
}

// Info logs an info message
func (l *Logger) Info(message string) {
	l.infoLog.Println(message)
}

// Infof logs a formatted info message
func (l *Logger) Infof(format string, args ...interface{}) {
	l.infoLog.Printf(format, args...)
}

// Warn logs a warning message
func (l *Logger) Warn(message string) {
	l.warnLog.Println(message)
}

// Warnf logs a formatted warning message
func (l *Logger) Warnf(format string, args ...interface{}) {
	l.warnLog.Printf(format, args...)
}

// Error logs an error message
func (l *Logger) Error(message string) {
	l.errorLog.Println(message)
}

// Errorf logs a formatted error message
func (l *Logger) Errorf(format string, args ...interface{}) {
	l.errorLog.Printf(format, args...)
}
