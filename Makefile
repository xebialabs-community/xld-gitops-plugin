export:
	xl --config config.yaml generate xl-deploy -s -p Configuration -f xebialabs/configuration.yaml
	xl --config config.yaml generate xl-deploy -s -p Applications -f xebialabs/applications.yaml
	xl --config config.yaml generate xl-deploy -s -p Infrastructure -f xebialabs/infrastructure.yaml
	xl --config config.yaml generate xl-deploy -s -p Environments -f xebialabs/environments.yaml

import:
	xl --config config.yaml apply -f xebialabs/configuration.yaml
	xl --config config.yaml apply -f xebialabs/applications.yaml
	xl --config config.yaml apply -f xebialabs/infrastructure.yaml
	xl --config config.yaml apply -f xebialabs/environments.yaml