mvn -f acount/pom.xml clean install -Dmaven.test.skip=true
mvn -f auth/pom.xml clean install -Dmaven.test.skip=true
mvn -f account-service/pom.xml clean package -Dmaven.test.skip=true
mvn -f p-auth-service/pom.xml clean package -Dmaven.test.skip=true
mvn -f gateway-service/pom.xml clean package -Dmaven.test.skip=true
mvn -f product-service/pom.xml clean package -Dmaven.test.skip=true
mvn -f order-service/pom.xml clean package -Dmaven.test.skip=true