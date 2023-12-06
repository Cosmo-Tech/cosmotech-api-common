# cosmotech-api-common
Local build
``` bash
./gradlew build
```
Package creation use last git tag to generate next version.
Update git tags with:
``` bash 
git pull --all
```
Assemble and publish package in local maven repository (~/.m2/repository/com/github/Cosmo-Tech/cosmotech-api-common/):
```
./gradlew assemble publishToMavenLocal
```
