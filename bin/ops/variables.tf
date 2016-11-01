variable "key_file" {}
variable "instance_type" {
    default = "m3.large"
}

variable "key_name" {
    default = "swanson"
}

variable "application" {
    default = "swanson"
}

variable "region" {
    default = "us-west-2"
}

variable "identifier" {
  default     = "swanson-rds"
  description = "Identifier for your DB"
}

variable "storage" {
  default     = "10"
  description = "Storage size in GB"
}

variable "engine" {
  default     = "postgres"
  description = "Engine type, example values mysql, postgres"
}

variable "engine_version" {
  description = "Engine version"

  default = {
    mysql    = "5.6.22"
    postgres = "9.4.1"
  }
}

variable "instance_class" {
  default     = "db.t2.micro"
  description = "Instance class"
}

variable "db_name" {
  default     = "swanson"
  description = "db name"
}

variable "username" {
  default     = "swanson-user"
  description = "User name"
}

variable "password" {
  description = "database password"
  default     = "database_password"
}

variable "cidr_blocks" {
  default     = "0.0.0.0/0"
  description = "CIDR for sg"
}

variable "sg_name" {
  default     = "rds_sg"
  description = "Tag Name for sg"
}

variable "vpc_id" {
  default    = "vpc_id"
  description = "VPC id"
}