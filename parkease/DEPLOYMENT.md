# Run ParkEase with Docker

This project uses Java 17 and stores H2 database files in `/app/data` inside the container.

## Before you start

Install and start Docker Desktop. In VS Code's terminal, change to the `parkease` folder (the folder containing this file and `pom.xml`).

## Build the image

```powershell
docker build -t parkease-web .
```

The first build downloads Maven and Java dependencies, so it may take a few minutes.

## Run the website

```powershell
docker run --name parkease -p 8081:8081 -v parkease-data:/app/data parkease-web
```

Open <http://localhost:8081/>. The named Docker volume `parkease-data` keeps the database files when the container is stopped and started again.

To stop the running container, press **Ctrl+C** in its terminal. To start that same container again later, run:

```powershell
docker start -ai parkease
```

## Deploy to a hosting provider

Choose a provider that can build a Dockerfile. Configure a persistent disk mounted at `/app/data` so H2 records remain available after a redeploy. The application reads the port from the `PORT` environment variable and uses `8081` when it is not set.

The H2 browser console is disabled by default. For local database inspection, set `H2_CONSOLE=true`; keep it disabled on a public deployment. The database folder is local to the app and is excluded from the Docker build context and Git.
