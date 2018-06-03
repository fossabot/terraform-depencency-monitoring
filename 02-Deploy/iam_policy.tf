# -- General
data "aws_caller_identity" "current" {}

data "aws_iam_policy_document" "lambda_assume_role" {

  "statement" {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

# -- KMS
data "aws_iam_policy_document" "kms_key_policy" {
  "statement" {
    sid = "Enable IAM User Permissions"
    principals {
      identifiers = ["arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"]
      type = "AWS"
    }
    actions = ["kms:*"]
    resources = ["*"]
  }

  "statement" {
    sid = "Allow access for Key Administrators"
    principals {
      identifiers = [
        "${var.assume_role}"
      ]
      type = "AWS"
    }
    actions = [
      "kms:Create*",
      "kms:Describe*",
      "kms:Enable*",
      "kms:List*",
      "kms:Put*",
      "kms:Update*",
      "kms:Revoke*",
      "kms:Disable*",
      "kms:Get*",
      "kms:Delete*",
      "kms:TagResource",
      "kms:UntagResource",
      "kms:ScheduleKeyDeletion",
      "kms:CancelKeyDeletion"
    ]
    resources = ["*"]
  }

  "statement" {
    sid = "Allow use of the key"
    principals {
      identifiers = [
        "${var.assume_role}",
        "${aws_iam_role.gather_terraform_modules_role.arn}",
        "${aws_iam_role.gather_github_repos_role.arn}",
        "${aws_iam_role.worker_role.arn}",
      ]
      type = "AWS"
    }
    actions = [
      "kms:Encrypt",
      "kms:Decrypt",
      "kms:ReEncrypt*",
      "kms:GenerateDataKey*",
      "kms:DescribeKey"
    ]
    resources = ["*"]
  }

  "statement" {
    sid = "Allow attachment of persistent resources"
    principals {
      identifiers = [
        "${var.assume_role}",
      ]
      type = "AWS"
    }
    actions = [
      "kms:CreateGrant",
      "kms:ListGrants",
      "kms:RevokeGrant"
    ]
    resources = ["*"]
    condition {
      test = "Bool"
      values = ["true"]
      variable = "kms:GrantIsForAWSResource"
    }
  }
}


# -- Lambda
data "aws_iam_policy_document" "basic_lambda_policy" {
  "statement" {
    resources = [
      "${aws_s3_bucket.terraform_dependencymonitoring_bucket.arn}",
      "${aws_s3_bucket.terraform_dependencymonitoring_bucket.arn}/*"
    ]

    actions = [
      "s3:GetObject",
      "s3:PutObject",
      "s3:PutObjectAcl",
    ]
  }

  "statement" {
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_policy" "basic_lambda_policy" {
  name   = "solt_tfm_dep_mon_basic_lambda_policy"
  path   = "/"
  policy = "${data.aws_iam_policy_document.basic_lambda_policy.json}"
}

data "aws_iam_policy_document" "basic_send_sqs_messages_policy" {
  "statement" {
    resources = [
      "${aws_sqs_queue.project_repo_queue.arn}",
    ]

    actions = [
      "sqs:GetQueueUrl",
      "sqs:SendMessage"
    ]
  }
}

resource "aws_iam_policy" "basic_send_sqs_messages_policy" {
  name   = "basic_send_sqs_messages_access_policy"
  path   = "/"
  policy = "${data.aws_iam_policy_document.basic_send_sqs_messages_policy.json}"
}

data "aws_iam_policy_document" "basic_receive_sqs_messages_policy" {
  "statement" {
    resources = [
      "${aws_sqs_queue.project_repo_queue.arn}",
    ]

    actions = [
      "sqs:GetQueueUrl",
      "sqs:ReceiveMessage"
    ]
  }
}

resource "aws_iam_policy" "basic_receive_sqs_messages_policy" {
  name   = "basic_receive_sqs_messages_access_policy"
  path   = "/"
  policy = "${data.aws_iam_policy_document.basic_receive_sqs_messages_policy.json}"
}

data "aws_iam_policy_document" "lambda_worker_dispatcher_policy" {
  "statement" {
    resources = [
      "${aws_lambda_function.worker_lambda.arn}",
    ]

    actions = [
      "lambda:InvokeFunction"
    ]
  }
}

resource "aws_iam_policy" "lambda_worker_dispatcher_policy" {
  name   = "lambda_worker_dispatcher_policy"
  path   = "/"
  policy = "${data.aws_iam_policy_document.lambda_worker_dispatcher_policy.json}"
}

data "aws_iam_policy_document" "basic_delete_sqs_messages_policy" {
  "statement" {
    resources = [
      "${aws_sqs_queue.project_repo_queue.arn}",
    ]

    actions = [
      "sqs:GetQueueUrl",
      "sqs:DeleteMessage"
    ]
  }
}

resource "aws_iam_policy" "basic_delete_sqs_messages_policy" {
  name   = "basic_delete_sqs_messages_access_policy"
  path   = "/"
  policy = "${data.aws_iam_policy_document.basic_delete_sqs_messages_policy.json}"
}