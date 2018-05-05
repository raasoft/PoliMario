package controlP5;

/**
 * controlP5 is a processing gui library.
 *
 *  2007-2010 by Andreas Schlegel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 * @author 		Andreas Schlegel (http://www.sojamo.de)
 * @modified	##date##
 * @version		##version##
 *
 */

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ControllerProperties {

	/**
	 * all ControllerProperties will be stored inside Map allProperties.
	 * ControllerProperties need to be unique or will otherwise be overwritten. A
	 * hashSet containing names of PropertiesSets is assigned to each
	 * ControllerProperty. HashSets are used instead of ArrayList to only allow
	 * unique elements.
	 */
	private Map<ControllerProperty, HashSet<String>> allProperties;

	/**
	 * A set of unique property-set names.
	 */
	private Set<String> allSets;

	private ControlP5 controlP5;

	private HashSet<PropertiesStorageFormat> _myStorageFormats;

	public ControllerProperties(ControlP5 theControlP5) {
		controlP5 = theControlP5;
		allProperties = new HashMap<ControllerProperty, HashSet<String>>();
		allSets = new HashSet<String>();
		_myStorageFormats = new HashSet<PropertiesStorageFormat>();
		// TODO _myStorageFormats.add(new XMLFormat());
		// TODO _myStorageFormats.add(new JSONFormat());
		_myStorageFormats.add(new SerializedFormat());
	}

	public Map<ControllerProperty, HashSet<String>> get() {
		return allProperties;
	}

	/**
	 * adds a property based on names of setter and getter methods of a
	 * controller.
	 * 
	 * @param thePropertySetter
	 * @param thePropertyGetter
	 */
	public ControllerProperty register(
			ControllerInterface theController,
			String thePropertySetter,
			String thePropertyGetter) {
		ControllerProperty p = new ControllerProperty(theController, thePropertySetter, thePropertyGetter);
		if (!allProperties.containsKey(p)) {
			allProperties.put(p, new HashSet<String>());
		}
		return p;
	}

	/**
	 * registering a property with only one parameter assumes that there is a
	 * setter and getter function present for the Controller. register("value")
	 * for example would create a property reference to setValue and getValue.
	 * Notice that the first letter of value is being capitalized.
	 * 
	 * @param theProperty
	 * @return
	 */
	public ControllerProperty register(ControllerInterface theController, String theProperty) {
		theProperty = Character.toUpperCase(theProperty.charAt(0)) + theProperty.substring(1);
		return register(theController, "set" + theProperty, "get" + theProperty);
	}

	public ControllerProperties remove(ControllerInterface theController, String theSetter, String theGetter) {
		ControllerProperty cp = new ControllerProperty(theController, theSetter, theGetter);
		allProperties.remove(cp);
		return this;
	}

	public ControllerProperties remove(ControllerInterface theController) {
		ArrayList<ControllerProperty> list = new ArrayList<ControllerProperty>(allProperties.keySet());
		for (ControllerProperty cp : list) {
			if (cp.controller.equals(theController)) {
				allProperties.remove(cp);
			}
		}
		return this;
	}

	public ControllerProperties remove(ControllerInterface theController, String theProperty) {
		return remove(theController, "set" + theProperty, "get" + theProperty);
	}

	public ArrayList<ControllerProperty> get(ControllerInterface theController) {
		ArrayList<ControllerProperty> props = new ArrayList<ControllerProperty>();
		ArrayList<ControllerProperty> list = new ArrayList<ControllerProperty>(allProperties.keySet());
		for (ControllerProperty cp : list) {
			if (cp.controller.equals(theController)) {
				props.add(cp);
			}
		}
		return props;
	}

	public ControllerProperty getProperty(ControllerInterface theController, String theSetter, String theGetter) {
		ControllerProperty cp = new ControllerProperty(theController, theSetter, theGetter);
		Iterator<ControllerProperty> iter = allProperties.keySet().iterator();
		while (iter.hasNext()) {
			ControllerProperty p = iter.next();
			if (p.equals(cp)) {
				return p;
			}
		}
		// in case the property has not been registered before, it will be
		// registered here automatically - you don't need to call
		// Controller.registerProperty() but can use Controller.getProperty()
		// instead.
		return register(theController, theSetter, theGetter);
	}

	public ControllerProperty getProperty(ControllerInterface theController, String theProperty) {
		theProperty = Character.toUpperCase(theProperty.charAt(0)) + theProperty.substring(1);
		return getProperty(theController, "set" + theProperty, "get" + theProperty);
	}

	public HashSet<ControllerProperty> getPropertySet(ControllerInterface theController) {
		HashSet<ControllerProperty> set = new HashSet<ControllerProperty>();
		Iterator<ControllerProperty> iter = allProperties.keySet().iterator();
		while (iter.hasNext()) {
			ControllerProperty p = iter.next();
			if (p.controller.equals(theController)) {
				set.add(p);
			}
		}
		return set;
	}

	public HashSet<String> addSet(String theSet) {
		allSets.add(theSet);
		return new HashSet<String>(allSets);
	}

	/**
	 * Moves a ControllerProperty from one set to another.
	 * 
	 * @param theProperty
	 * @param theSet
	 */
	public void move(ControllerProperty theProperty, String fromSet, String toSet) {
		if (!exists(theProperty)) {
			return;
		}
		if (allProperties.containsKey(theProperty)) {
		}
	}

	public void move(ControllerInterface theController, String fromSet, String toSet) {

	}

	/**
	 * copies a ControllerProperty from one set to other(s);
	 * 
	 * @param theProperty
	 * @param theSet
	 */
	public void copy(ControllerProperty theProperty, String... theSet) {
		if (!exists(theProperty)) {
			return;
		}

		for (String s : theSet) {
			allProperties.get(theProperty).add(s);
			if (!allSets.contains(s)) {
				addSet(s);
			}
		}
	}

	public void copy(ControllerInterface theController, String... theSet) {
		HashSet<ControllerProperty> set = getPropertySet(theController);
		for (ControllerProperty cp : set) {
			copy(cp, theSet);
		}
	}

	/**
	 * removes a ControllerProperty from one or multiple sets.
	 * 
	 * @param theProperty
	 * @param theSet
	 */
	public void remove(ControllerProperty theProperty, String... theSet) {
		if (!exists(theProperty)) {
			return;
		}
		for (String s : theSet) {
			allProperties.get(theProperty).remove(s);
		}
	}

	public void remove(ControllerInterface theController, String... theSet) {

	}

	/**
	 * stores a ControllerProperty in one particular set only.
	 * 
	 * @param theProperty
	 * @param theSet
	 */
	public void only(ControllerProperty theProperty, String theSet) {
		// clear all the set-references for a particular property
		allProperties.get(theProperty).clear();
		// add theSet to the empty collection of sets for this particular property
		allProperties.get(theProperty).add(theSet);
	}

	public void only(ControllerInterface theController, String... theSet) {

	}

	private boolean exists(ControllerProperty theProperty) {
		return allProperties.containsKey(theProperty);
	}

	/**
	 * deletes a ControllerProperty from all Sets including the default set.
	 * 
	 * @param theProperty
	 */
	public void delete(ControllerProperty theProperty) {
		if (!exists(theProperty)) {
			return;
		}
		allProperties.remove(theProperty);
	}

	boolean updatePropertyValue(ControllerProperty theProperty) {
		Method method;
		try {
			method = theProperty.controller.getClass().getMethod(theProperty.getter);
			Object value = method.invoke(theProperty.controller);
			theProperty.type = method.getReturnType();
			theProperty.value = value;
			if (checkSerializable(value)) {
				return true;
			}
		} catch (Exception e) {
			ControlP5.logger().severe("" + e);
		}
		return false;
	}

	private boolean checkSerializable(Object theProperty) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(out);
			stream.writeObject(theProperty);
			stream.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void compile(String thePropertiesPath) {
		compile(allProperties.keySet(), thePropertiesPath);
	}

	protected void compile(Set<ControllerProperty> theProperties, String thePropertiesPath) {
		for (PropertiesStorageFormat msf : _myStorageFormats) {
			msf.compile(theProperties, thePropertiesPath);
		}
	}

	public String getXML(ControllerProperty theProperty) {
		// if(value.getClass().isArray()) {
		//		
		// int length = Array.getLength(value);
		// System.out.println(length);
		// for (int idx = 0; idx < length; ++idx) {
		// Object item = Array.get(value, idx);
		//			
		// if(item.getClass().isArray()) {
		// for (int i = 0; i < Array.getLength(item); ++i) {
		// System.out.print(Array.get(item, i).getClass()+"="+Array.get(item, i));
		// }
		// System.out.println("");
		// }
		// }
		// }

		// Mapping Between JSON and Java Entities
		// http://code.google.com/p/json-simple/wiki/MappingBetweenJSONAndJavaEntities

		String s = "\t<property>\n";
		s += "\t\t<name>" + theProperty.name + "</name>\n";
		s += "\t\t<class>" + ControlP5IOHandler.formatGetClass(theProperty.controller.getClass()) + "</class>\n";
		s += "\t\t<setter>" + theProperty.setter + "</setter>\n";
		s += "\t\t<getter>" + theProperty.getter + "</getter>\n";
		s += "\t\t<type>" + ControlP5IOHandler.formatGetClass(theProperty.type) + "</type>\n";
		s += "\t\t<value><![CDATA["
				+ (theProperty.value.getClass().isArray() ? ControlP5IOHandler.get(theProperty.value) : theProperty.value)
				+ "]]></value>\n";
		s += "\t</property>\n";
		return s;
	}

	public boolean load(String thePropertiesPath) {
		try {
			FileInputStream fis = new FileInputStream(thePropertiesPath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			int size = ois.readInt();
			ControlP5.logger().info("loading " + size + " property-items.");

			for (int i = 0; i < size; i++) {

				try {
					ControllerProperty cp = (ControllerProperty) ois.readObject();
					ControllerInterface ci = controlP5.controller(cp.name);
					ci = (ci == null) ? controlP5.group(cp.name) : ci;
					Method method;
					try {
						method = ci.getClass().getMethod(cp.setter, new Class[] { cp.type });
						method.setAccessible(true);
						method.invoke(ci, new Object[] { cp.value });
					} catch (Exception e) {
						ControlP5.logger().severe(e.toString());
					}

				} catch (Exception e) {
					ControlP5.logger().warning("skipping a property, " + e);
				}
			}
			ois.close();
		} catch (Exception e) {
			ControlP5.logger().warning("Exception during deserialization: " + e);
			return false;
		}
		return true;
	}

	protected boolean save(String thePropertiesPath) {
		compile(thePropertiesPath);
		return true;
	}

	public void save(String thePropertiesPath, String... theSets) {
		HashSet<ControllerProperty> sets = new HashSet<ControllerProperty>();
		Iterator<ControllerProperty> iter = allProperties.keySet().iterator();
		while (iter.hasNext()) {
			ControllerProperty p = iter.next();
			HashSet<String> set = allProperties.get(p);
			for (String str : set) {
				for (String s : theSets) {
					if (str.equals(s)) {
						sets.add(p);
					}
				}
			}
		}
		compile(sets, thePropertiesPath);
	}

	public String toString() {
		String s = "";
		s += this.getClass().getName() + "\n";
		s += "total num of properties:\t" + allProperties.size() + "\n";
		for (ControllerProperty c : allProperties.keySet()) {
			s += "\t" + c + "\n";
		}
		s += "total num of sets:\t\t" + allSets.size() + "\n";
		for (String set : allSets) {
			s += "\t" + set + "\n";
		}
		return s;
	}

	public interface PropertiesStorageFormat {
		public void compile(Set<ControllerProperty> theProperties, String thePropertiesPath);
	}

	private class XMLFormat implements PropertiesStorageFormat {
		public void compile(Set<ControllerProperty> theProperties, String thePropertiesPath) {
			StringBuffer xml = new StringBuffer();
			xml.append("<properties>\n");
			for (ControllerProperty cp : theProperties) {
				if (cp.active) {
					xml.append(getXML(cp));
				}
			}
			xml.append("</properties>");
			System.out.println(xml.toString());
		}
	}

	private class JSONFormat implements PropertiesStorageFormat {
		public void compile(Set<ControllerProperty> theProperties, String thePropertiesPath) {
		}
	}

	private class SerializedFormat implements PropertiesStorageFormat {

		public void compile(Set<ControllerProperty> theProperties, String thePropertiesPath) {
			int active = 0;
			int total = 0;
			HashSet<ControllerProperty> propertiesToBeSaved = new HashSet<ControllerProperty>();
			for (ControllerProperty cp : theProperties) {
				if (cp.active) {
					if (updatePropertyValue(cp)) {
						active++;
						propertiesToBeSaved.add(cp);
					}
				}
				total++;
			}

			int ignored = total - active;

			try {
				FileOutputStream fos = new FileOutputStream(thePropertiesPath);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				ControlP5.logger().info("Saving property-items to " + thePropertiesPath);
				oos.writeInt(active);

				for (ControllerProperty cp : propertiesToBeSaved) {
					if (cp.active) {
						oos.writeObject(cp);
					}
				}

				ControlP5.logger().info(active + " items saved, " + (ignored) + " items ignored. Done saving properties.");
				oos.flush();
				oos.close();
				fos.close();
			} catch (Exception e) {
				ControlP5.logger().warning("Exception during serialization: " + e);
			}
		}
	}
}
