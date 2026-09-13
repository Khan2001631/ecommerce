# VPS Deployment — Server Inventory & Safety Notes

> **Purpose:** Document the current state of the shared VPS before deploying the e-commerce application.
>
> **Important:** This is a shared VPS. The existing application belongs to a colleague. Do not modify, restart, stop, upgrade, uninstall, or reconfigure his application/services unless explicitly agreed with him.

---

## 1. My VPS User / Access

**Logged-in user:**

```text
mohammad
```

**Home directory:**

```text
/home/mohammad
```

**User ID / groups:**

```text
uid=1000(mohammad) gid=1000(mohammad) groups=1000(mohammad),27(sudo),100(users)
```

### Permissions

- `mohammad` has membership in the `sudo` group.
- Administrative commands can therefore be run with `sudo`.
- Having `sudo` access does **not** mean it is safe to modify existing services. This is a shared machine.

---

# 2. VPS Resources

Current resource information:

| Resource | Current value |
|---|---:|
| CPU | 2 cores |
| RAM | 7.8 GiB |
| RAM currently available | ~6.2 GiB |
| Swap | 0 B |
| Root disk | 96 GB |
| Root disk used | 14 GB |
| Root disk available | 82 GB |
| Root disk usage | 15% |

### Notes

- There is currently **no swap** configured.
- The machine has reasonable available memory and disk space for beginning the deployment.
- Only 2 CPU cores are available, so the Java API, Python AI service, MySQL, and other containers should be monitored for CPU/memory usage.

---

# 3. Operating System / Server

The server is an Ubuntu system.

The exact OS release was not recorded during this inventory. Run the following later if needed:

```bash
cat /etc/os-release
```

The filesystem root is:

```text
/
```

Standard directories such as `/etc`, `/var`, `/home`, `/usr`, `/opt`, etc. are present.

---

# 4. My Home Directory

My home directory is:

```text
/home/mohammad
```

Current contents observed:

```text
.bash_history
.bash_logout
.bashrc
.cache/
.profile
.sudo_as_admin_successful
```

This directory is currently essentially clean and can be used for user-specific files if required.

---

# 5. Existing Colleague Application — IMPORTANT

The colleague already has an application running on this VPS.

## 5.1 Tomcat

Tomcat 10 is installed and running.

Service:

```text
tomcat10.service
```

Status observed:

```text
active (running)
```

Tomcat's main directory:

```text
/var/lib/tomcat10/
```

Contents observed:

```text
conf/
lib/
logs/
policy/
webapps/
work/
```

### Protected Tomcat location

The colleague specifically told me that his application is located under:

```text
/var/lib/tomcat10/webapps
```

### DO NOT TOUCH

Do not:

- delete files
- modify deployed applications
- replace WAR files
- restart Tomcat unnecessarily
- change Tomcat configuration
- change Tomcat ports
- change Tomcat libraries
- modify anything under `/var/lib/tomcat10/`

unless explicitly agreed with the colleague.

---

# 6. Colleague's Python / Counsel Service

There is also an existing Python service.

The process was identified as:

```text
/opt/iqg/counsel-service/venv/bin/python3
```

Process:

```text
uvicorn
```

It is listening on:

```text
127.0.0.1:8000
```

The corresponding systemd service is:

```text
counsel.service
```

Description:

```text
Iqra Counsel embedding service
```

Status observed:

```text
active (running)
```

### DO NOT TOUCH

Treat the following as belonging to the colleague:

```text
/opt/iqg/counsel-service
counsel.service
127.0.0.1:8000
```

Do not:

- stop the service
- restart the service
- modify its virtual environment
- modify its code
- change its port
- delete or replace files
- upgrade its dependencies

unless explicitly agreed with the colleague.

---

# 7. Existing Nginx

Nginx is installed:

```text
nginx/1.24.0 (Ubuntu)
```

Service:

```text
nginx.service
```

Status:

```text
active (running)
```

Nginx is enabled as a system service.

## Nginx ports

Nginx is currently listening on:

```text
0.0.0.0:80
0.0.0.0:443
[::]:80
```

Therefore:

```text
80  → occupied
443 → occupied
```

These ports should be considered **in use**.

---

# 8. Existing Nginx Routing

The current Nginx configuration contains:

```text
server_name iqg.life www.iqg.life;
```

Nginx listens for:

```text
iqg.life
www.iqg.life
```

The current reverse proxy is:

```text
proxy_pass http://127.0.0.1:8080/;
```

Therefore the colleague's current traffic flow is approximately:

```text
Internet
   |
   v
Nginx :80 / :443
   |
   v
127.0.0.1:8080
   |
   v
Tomcat / Java application
```

### IMPORTANT

Nginx is already actively serving the colleague's application.

**Do not modify the existing Nginx configuration casually.**

In particular, do not:

```bash
sudo systemctl restart nginx
```

or edit Nginx configuration without first understanding the impact on:

```text
iqg.life
www.iqg.life
```

and the existing Tomcat application.

---

# 9. Ports Currently Identified

| Port | Address | Current owner/use | Status |
|---:|---|---|---|
| 22 | system | SSH | In use |
| 80 | 0.0.0.0 / IPv6 | Nginx | In use |
| 443 | 0.0.0.0 | Nginx | In use |
| 8000 | 127.0.0.1 | Colleague's Uvicorn / counsel service | In use |
| 8080 | 127.0.0.1 | Colleague's Tomcat / Java | In use |
| 3306 | — | No listener observed | Appears available |

The check for ports included:

```text
:3306
:8000
:8080
:8081
```

Only `8000` and `8080` returned listeners.

### Important

Port `8081` was not observed as occupied during that check.

Port `3306` was not observed as occupied.

These should still be rechecked immediately before deployment because server state can change.

---

# 10. Docker

Docker is **not currently installed**.

Command:

```bash
docker --version
```

returned:

```text
Command 'docker' not found
```

Ubuntu suggested:

```text
sudo apt install docker.io
```

but Docker has **not been installed yet**.

### Important

Do not install Docker until the deployment plan is agreed, because this is a shared VPS.

---

# 11. MySQL

MySQL command was not available:

```text
mysql --version
```

indicated that MySQL is not installed/available through the current command.

No listener was observed on the standard MySQL port:

```text
3306
```

Therefore MySQL appears to be available for our eventual deployment, subject to a final check before installation.

---

# 12. Existing `/opt` Directory

The `/opt` directory currently contains:

```text
/opt/iqg
```

This is related to the colleague's existing application.

We have already identified:

```text
/opt/iqg/counsel-service
```

### DO NOT USE

Do not put the new e-commerce application inside:

```text
/opt/iqg
```

Do not modify files under:

```text
/opt/iqg/counsel-service
```

---

# 13. Recommended Space for My Application

No directory for the new e-commerce application has been created yet.

A separate directory should be used, for example:

```text
/opt/ecommerce/
```

This has **not yet been created**.

The important separation should be:

```text
/var/lib/tomcat10/       ← COLLEAGUE — DO NOT TOUCH

/opt/iqg/                ← COLLEAGUE — DO NOT TOUCH

/opt/ecommerce/          ← OUR APPLICATION — TO BE CREATED
```

The exact location can be finalized before deployment.

---

# 14. Running System Services Observed

The following services were observed as active:

```text
counsel.service
cron.service
dbus.service
getty@tty1.service
monarx-agent.service
nginx.service
polkit.service
qemu-guest-agent.service
rsyslog.service
serial-getty@ttyS0.service
ssh.service
systemd-journald.service
systemd-logind.service
systemd-networkd.service
systemd-resolved.service
systemd-timesyncd.service
systemd-udevd.service
tomcat10.service
unattended-upgrades.service
user@1000.service
```

### Application-related services to protect

The most relevant existing application services are:

```text
nginx.service
tomcat10.service
counsel.service
```

The remaining services are primarily operating-system, security, networking, logging, or virtualization services.

Do not stop/restart system services without understanding their purpose.

---

# 15. Current Server Architecture — Colleague

Based on the checks performed so far:

```text
                         INTERNET
                            |
                            v
                    +---------------+
                    |     NGINX     |
                    |    :80/:443   |
                    +-------+-------+
                            |
                            | proxy_pass
                            v
                    127.0.0.1:8080
                            |
                            v
                    +---------------+
                    |    TOMCAT 10  |
                    +---------------+
                            |
                            v
              /var/lib/tomcat10/webapps


                    Separate service
                            |
                            v
                    127.0.0.1:8000
                            |
                            v
                    Uvicorn / Python
                            |
                            v
              /opt/iqg/counsel-service
```

---

# 16. What We Must NOT Touch

Until explicitly agreed otherwise, consider these protected:

### Files/directories

```text
/var/lib/tomcat10/
/var/lib/tomcat10/webapps/
/opt/iqg/
/opt/iqg/counsel-service/
```

### Services

```text
tomcat10.service
counsel.service
nginx.service
```

### Existing ports

```text
80
443
127.0.0.1:8000
127.0.0.1:8080
```

Especially avoid changing the existing Nginx configuration because it currently routes:

```text
iqg.life
www.iqg.life
        |
        v
127.0.0.1:8080
```

---

# 17. Our Intended Application

The application we want to deploy is an e-commerce application consisting of:

```text
E-commerce
│
├── Frontend
│   └── React
│
└── Backend
    ├── Java API
    │   └── Spring Boot
    │
    └── Python AI
```

Database:

```text
MySQL
```

The intended deployment will likely use:

```text
Docker
Docker Compose
```

but these have **not yet been installed/configured**.

---

# 18. Important Deployment Constraint

This is a **shared VPS**, not a dedicated machine.

Therefore the deployment must follow the principle:

```text
Existing colleague application
        ↓
        KEEP UNCHANGED

Our application
        ↓
Separate directories
Separate containers
Separate ports
Separate configuration
Separate environment variables
```

We should avoid changing global server configuration unless necessary.

---

# 19. What Has NOT Been Done Yet

As of this inventory:

- Docker has **not** been installed.
- Docker Compose has **not** been installed/configured.
- MySQL has **not** been installed.
- No application directory has been created for the e-commerce project.
- No Dockerfiles have been created on the VPS.
- No Docker containers have been started.
- No Nginx configuration has been changed.
- No Tomcat configuration has been changed.
- No colleague service has been stopped/restarted.
- No firewall rules have been changed.
- No ports have been changed.
- No production deployment has started.

This means the VPS is currently unchanged by the deployment work.

---

# 20. Next Planned Work

Before making server changes, inspect the local e-commerce project and design the container setup.

Likely sequence:

```text
1. Inspect local project structure
        ↓
2. Create Dockerfile for React
        ↓
3. Create Dockerfile for Spring Boot
        ↓
4. Create Dockerfile for Python AI
        ↓
5. Create Docker Compose configuration
        ↓
6. Decide how MySQL will run
        ↓
7. Test entire stack locally
        ↓
8. Install Docker on VPS
        ↓
9. Deploy into a separate directory
        ↓
10. Configure networking/ports
        ↓
11. Integrate with Nginx carefully
        ↓
12. Configure domain/HTTPS if required
        ↓
13. Later automate deployment with CI/CD
```

**Do not skip the local Docker testing phase.** It is preferable to solve Docker/application issues locally before introducing the shared VPS into the debugging process.
