Convert an object to JSON

implemented in java 17

# Examples

User class:

```
public class User extends JSONCustom {

	@FieldJSON(method=GET)
	private String name;
	
	@FieldJSON(method=GET)
	private int age;
	
	@FieldJSON(method=GET)
	private boolean isMen;
	
	
	@ConstructorJSON(arguments={String.class, boolean.class}, names={"name", "isMen"})
	public User(String name, boolean isMen) {
		this.name = name;
		this.isMen = isMen;
		age = -1;
	}
	
	@SetMethodJSON(arguments= {int.class}, names= {"age"})
	public void setAge(int age) {
		if(age < 0) {
			age = 3000;
		}
		this.age = age;
	}
	
	public User(String name, int age, boolean isMen) {
		this.name = name;
		this.age = age;
		this.isMen = isMen;
	}
	
	public String getName() {
		return name;
	}
	
	public int getAge() {
		return age;
	}
	
	public String getGender() {
		return isMen ? "Men" : "Women";
	}
	
}
```

code:

```
User user = new User("Tom", 56, true);
JSONObject json = user.toJSON();
```

JSON:

```
{
	"isMen" : true,
	"name": "Tom",
	"age": 56 	
}
```

Convert an JSON to object:

```
User user = JSONCreater.toObject(json, User.class);
```

# Connect

maven:

```
<dependency>
  		<groupId>ru.object-cat.jsonstruct</groupId>
  		<artifactId>json-creater</artifactId>
		<version>1.0.0</version>
  	</dependency>
```

module java:

```
requires ru.objectcat.base;
requires ru.objectcat.create;
```
