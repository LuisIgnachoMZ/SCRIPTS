# Sistema de Inventario

Este proyecto es un sistema de inventario básico desarrollado en Java, utilizando MySQL como base de datos y Apache Tomcat como servidor de aplicaciones.

---

## Tecnologías y Herramientas Utilizadas

- **IDE utilizado**: Visual Studio Code (VS Code), DBeaver versión 24.1.5
- **Lenguaje de programación**: Java, versión OpenJDK "21.0.5" (2024-10-15 LTS) Corretto-21.0.5.11.1
- **DBMS utilizado**: MySQL Community Server
  - **Versión**: 8.4.3 (GPL)

---

## Requisitos

- **JDK 17** o superior (probado con Amazon Corretto JDK).
- **MySQL 8.4.3 Community Edition**.
- **Apache Tomcat** para ejecutar Java Servlets y manejar las solicitudes HTTP del front-end.

---

## Instalación de Apache Tomcat

### En macOS
1. Descarga y descomprime Apache Tomcat:
   ```bash
   tar -xvzf apache-tomcat-10.1.31.tar.gz

2. Mueve la carpeta de Tomcat a una ubicación permanente:
   ```bash
   sudo mv apache-tomcat-10.1.31 /usr/local/tomcat
3. Da permisos de ejecución a los scripts de Tomcat:
   ```bash
   sudo chmod +x /usr/local/tomcat/bin/*.sh

### En Windows
1. Descarga Apache Tomcat desde https://tomcat.apache.org/.

2. Extrae el contenido del archivo descargado en una carpeta de tu elección, por ejemplo, C:\tomcat.

3. No es necesario otorgar permisos adicionales, pero asegúrate de configurar las variables de entorno correctamente (opcional):

Agrega C:\tomcat\bin a tu variable de entorno PATH para poder ejecutar comandos de Tomcat desde cualquier ubicación.

---

## Compilación y Ejecución del Sistema
1. Abre una terminal (o Símbolo del sistema en Windows) en el directorio raíz del proyecto (inventario-sistema).

2. Compilación del código
Compila el código Java del proyecto:

### En macOS:
```bash
   cd /usr/local/tomcat/webapps/inventario-sistema/src/main/java
   javac -cp "/usr/local/tomcat/webapps/inventario-sistema/WEB-INF/lib/*" com/castores/inventario/*.java -d /usr/local/tomcat/webapps/inventario-sistema/WEB-INF/classes

### En Windows:
```bash
   cd C:\tomcat\webapps\inventario-sistema\src\main\java
   javac -cp "C:\tomcat\webapps\inventario-sistema\WEB-INF\lib\*" com\castores\inventario\*.java -d C:\tomcat\webapps\inventario-sistema\WEB-INF\classes

---

### Creación e Importación de la Base de Datos
Crea e importa la base de datos desde el archivo inventario_sistema.sql:

### En macOS:
```bash
   sudo mysql -u root -p inventario_sistema < /usr/local/tomcat/webapps/inventario-sistema/inventario_sistema.sql

### En Windows:
```bash
   mysql -u root -p inventario_sistema < C:\tomcat\webapps\inventario-sistema\inventario_sistema.sql

---

Iniciar y Detener Tomcat
Inicia Tomcat:

### En macOS:
```bash
   /usr/local/tomcat/bin/startup.sh

### En windows: 
```bash
   C:\tomcat\bin\startup.bat

Para detener Tomcat:

### En macOS:
```bash
   /usr/local/tomcat/bin/shutdown.sh

### En windows: 
```bash
   C:\tomcat\bin\shutdown.bat
