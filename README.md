# GraphQL Server

A comprehensive guide for developing, building, and deploying GraphQL Server with MongoDB and Angular frontend.

## Development

### Run for Development

```bash
gradle bootRun
```

### Build Application

```bash
# Build with bootJar task
./gradlew clean bootJar

# Run the generated JAR from build/libs/
java -jar build/libs/your-app-0.0.1-SNAPSHOT.jar

# systemd service file uses --spring.profiles.active=prod to use application-prod.properties file
/usr/bin/java -jar /opt/graphql_server/graphql_server.jar --spring.profiles.active=prod
```

## Deployment

### GraphQL Server Deployment on Ubuntu

#### Copy JAR to Server

```bash
scp build/libs/graphql_server-0.0.1-SNAPSHOT.jar user@server:/opt/graphql_server/graphql_server.jar
```

#### SystemD Service Configuration

Example systemd service file can be found at:

```
deploy/graphql_server.service
```

Save the service file to:

```
/etc/systemd/system/graphql_server.service
```

#### Service Management

```bash
# Update service to point to new JAR
sudo systemctl daemon-reload
sudo systemctl start graphql_server

# Check status
sudo systemctl status graphql_server

# Restart after changing file
sudo systemctl restart graphql_server

# Check server output logs
journalctl -u graphql_server.service
```

### MongoDB Deployment

#### Installation

Follow the official MongoDB installation guide:
https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu/

MongoDB runs with systemd on port 27017.

#### MongoDB Management

```bash
# Check service status
sudo systemctl status mongod

# Get help
mongod -h
mongosh -h
```

#### Database Operations

```bash
# Open mongosh terminal
mongosh

# Show databases
show dbs

# Use specific database
use todoList

# Show collections
show collections

# Find documents in collection
db.customer.find()
```

### Angular Frontend Deployment

#### Build Angular Application

```bash
ng build --configuration=production
```

#### Deploy to Server

```bash
# Move built files to server
scp todo-app/dist/todo-app/browser/* user@server:/var/www/subDomain.domain.topLvlDomain/
```

### Nginx Configuration

#### Setup Site Configuration

Nginx configuration files are located in the `deploy` directory.

Copy configuration files to nginx sites-available:

```bash
# Copy to /etc/nginx/sites-available/
```

#### Enable Sites

```bash
# Create symbolic links from available files to enabled files
sudo ln -s /etc/nginx/sites-available/domain.topLvlDomain /etc/nginx/sites-enabled/
sudo ln -s /etc/nginx/sites-available/subDomain.domain.topLvlDomain/etc/nginx/sites-enabled/
```

#### Nginx Management

```bash
# Start nginx
sudo systemctl start nginx

# Stop nginx
sudo systemctl stop nginx

# Restart nginx
sudo systemctl restart nginx

# Reload configuration
sudo systemctl reload nginx

# Check status
sudo systemctl status nginx
```

## Security

### Firewall Configuration with UFW

#### Check Status

```bash
sudo ufw status
```

Configuration file location: `/etc/default/ufw`

#### Default Rules

```bash
sudo ufw default allow outgoing
sudo ufw default deny incoming
```

#### Allow Specific Services

```bash
# Allow SSH
sudo ufw allow ssh

# Rate limit SSH
sudo ufw limit ssh/tcp comment 'Rate limit for openssh server'

# Allow HTTP (Angular app)
sudo ufw allow 80/tcp

# Allow HTTPS (Angular app)
sudo ufw allow 443/tcp

# Allow GraphQL server
sudo ufw allow 8080/tcp
```
