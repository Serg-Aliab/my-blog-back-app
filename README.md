
### Порядок деплоя бэкенда в сервлет-контейнер

Наш проект должен запускаться в сервере Tomcat по корневому адресу, то есть по localhost:8080
Что для этого нужно сделать:

1. Остановить сервер tomcat

2. Создать папку {catalina.home}/deploy и скопировать в нее war файл нашего проекта
({catalina.home} это корневая папка сервера Tomcat)

3. Создать файл ROOT.xml в папке $CATALINA_HOME\conf\Catalina\localhost со следующим содержимым:

```
   Context docBase="${catalina.home}/deploy/{имя_файла_war}"/>
```
  в моем случае {имя_файла_war} будет:
```
  my-blog-back-app-0.0.1-SNAPSHOT.war
```

4. Удалить папку $CATALINA_HOME\webapps\ROOT (после запуска Tomcat она будет пересоздана
по заданному нами контексту)

5. Запустить сервер tomcat. Теперь по адресу localhost:8080 будет сидеть корень нашего проекта,
 например, localhost:8080/api/posts

6. В build.gradle создана task (под названием myBuild) на сборку нашего проекта,
с последующем копированием файла .war в папку сервера Tomcat "${catalina.home}/deploy".

##### build.gradle:
```
def props = new Properties()
file("gradle.properties").withInputStream { props.load(it) }

tasks.register('myBuild', Copy) {
	dependsOn("build")
	from  'build/libs'
	include "*.war"
	into props.getProperty("tomcat.path") + '/' + props.getProperty("tomcat.deploy.folder")
}
```

Пути к серверу Tomcat и папке deploy прописываются отдельно в:

##### gradle.properties:
```
tomcat.path=apache-tomcat-10.1.49
tomcat.deploy.folder=deploy
```

Теперь после каждой пересборки нашего проекта задачей myBuild, исполнимый проектный war-файл
будет копироваться в корневой контекст сервера Tomcat и обновляться. Обновление сервера происходит
после копирования файла war в контекст localhost/ROOT автоматически (это будет происходить
не мгновенно, иногда до 3-сек).
