#!/bin/bash
echo "Initializing LocalStack resources..."

# Wait for LocalStack to be ready
export AWS_ACCESS_KEY_ID="test"
export AWS_SECRET_ACCESS_KEY="test"
export AWS_DEFAULT_REGION="us-east-1"

# Create S3 Bucket
echo "Creating S3 bucket 'customer-statements'..."
awslocal s3 mb s3://customer-statements

# Create SQS Queue
echo "Creating SQS queue 'statement-ingestion-queue'..."
awslocal sqs create-queue --queue-name statement-ingestion-queue

echo "LocalStack initialization complete!"
