how to see network and remove network in docker

To list all Docker networks, use the command `docker network ls`, which displays a table of network IDs, names, drivers, and scopes. To inspect detailed configuration for a specific network, use `docker network inspect <network_name_or_id>`.

To remove a specific network, use `docker network rm <network_name_or_id>`. **Crucial Requirement**: The network must not have any connected containers. If containers are attached, you must first disconnect them using `docker network disconnect <network> <container>` or remove the containers entirely before the network deletion will succeed.

To remove all unused networks at once, use `docker network prune`. This command removes any custom networks not currently referenced by at least one running or stopped container. You can bypass the confirmation prompt by adding the `-f` or `--force` flag.

---
