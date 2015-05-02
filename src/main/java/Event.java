

public class Event
{
	private String name;
	
	private String designation;
	
	private String date;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getDesignation()
	{
		return designation;
	}

	public void setDesignation(String designation)
	{
		this.designation = designation;
	}
	
	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String toString()
	{
		StringBuilder newString = new StringBuilder();
		newString.append("Name:");
		newString.append(name);
		newString.append("\nDesignation:");
		newString.append(designation);
		newString.append("\nDate:");
		newString.append(date);
		return newString.toString();
	}
}