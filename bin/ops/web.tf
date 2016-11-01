resource "aws_instance" "server" {
    ami = "ami-281eee48"

    instance_type = "m3.large"

    tags {
        Name = "${var.application} web"
        Project = "${var.application}"
        Roles = "app"
        Options = "primary"
    }
    lifecycle {
        create_before_destroy = true
    }
    root_block_device {
        volume_size = "50"
        delete_on_termination = true
    }
    connection {
        user = "ubuntu"
        key_file = "${var.key_file}"
    }
    provisioner "remote-exec" {
        inline = [
            "host=$(hostname)",
            "echo 127.0.0.1 $host | sudo tee -a /etc/hosts"
        ]
    }
}

