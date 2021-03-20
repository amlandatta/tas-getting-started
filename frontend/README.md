# A simple static app

```shell
├── Staticfile
└── index.html
```

Here `Staticfile` is an empty file which acts as a marker using which CF decides to use [staticfile_buildpack](https://docs.cloudfoundry.org/buildpacks/staticfile/index.html) i.e. deploys an NGINX container.

### Login to TAS (Cloud Foundry)

```shell
cf login -a https://api.tas-domain \
    -u USERNAME -p PASSWORD --skip-ssl-validation \
    -o ORGNAME -s SPACENAME
#tas-domain = cf api domain
```

### Deploy to TAS

```shell
cd frontend

cf push hello-app -p .
# or
cf push hello-app -p . -b staticfile_buildpack
```

### Check applicatiion status

```shell
cf app hello-app

Showing health and status for app hello-app in org ORGNAME / space SPACENAME as USERNAME...

name:              hello-app
requested state:   started
routes:            hello-app.tas-app-domain
last uploaded:     ...
stack:             cflinuxfs3
buildpacks:
	name                   version   detect output   buildpack name
	staticfile_buildpack   1.5.12    staticfile      staticfile

type:           web
sidecars:
instances:      1/1
memory usage:   1024M
     state     since                  cpu    memory        disk       details
#0   running   2021-03-19T11:31:19Z   0.4%   14.3M of 1G   6M of 1G
```

Launch application from your browser using https://hello-app.<tas-app-domain>, where tas-app-domain = default app domain

```
# List all apps
cf apps

# View app details
cf app hello-app

# View app details
cf app hello-app

# View app details
cf ssh hello-app

# Delete app
cf delete hello-app
```

Note: SSH may not work in your environment if ssh is disabled. As a best practise, ssh should be disabled in production environment. 