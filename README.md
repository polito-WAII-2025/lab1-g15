[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/vlo9idtn)

# Lab 1 - Group 15

## Getting started

In order to correctly execute the application:

1. Ensure that the `evaluation` folder is present at the root level of this repository.
2. Adjust or create the two main files `custom-parameters.yml` and `waypoints.csv`.
3. Run the application with Docker:
    - using docker compose:
   ```bash
    $ docker compose up
   ```

    - by manually building the container:
   ```bash
    $ docker build -t route-analyzer ./RouteAnalyzer
    $ docker run --rm -v "$(pwd)/evaluation:/app/resources" route-analyzer
   ```

## Customization

It is possible to change mount points and file names directly inside the `docker-compose.yml` file or by changing the
corresponding environment variables when running the container: `CONFIG_FILE` and `WAYPOINTS_FILE`.
