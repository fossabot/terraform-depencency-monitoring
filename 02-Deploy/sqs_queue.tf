resource "aws_sqs_queue" "project_repo_queue" {
  name                      = "solt-terraform-depmon-repos-queue"
  delay_seconds             = 0
  message_retention_seconds = 86400 // 1 day
  visibility_timeout_seconds = 300 // 5 min - (Max time a lambda function can execute)
  redrive_policy            = "{\"deadLetterTargetArn\":\"${aws_sqs_queue.project_repo_dead_letter_queue.arn}\",\"maxReceiveCount\":3}"
}

resource "aws_sqs_queue" "project_repo_dead_letter_queue" {
  name                      = "solt-terraform-depmon-repos-queue-dead-letter"
  delay_seconds             = 0
  message_retention_seconds = 1209600 // 14 days
}