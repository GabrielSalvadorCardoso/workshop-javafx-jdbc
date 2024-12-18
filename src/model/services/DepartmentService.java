package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll() {
		List<Department> departments = new ArrayList<Department>();
//		departments.add(new Department(1, "Books"));
//		departments.add(new Department(2, "Computers"));
//		departments.add(new Department(3, "Electronics"));
		return this.dao.findAll();
		//return departments;
	}
	
	public void saveOrUpdate(Department obj) {
		if(obj.getId() == null) {
			this.dao.insert(obj);
		} else {
			this.dao.update(obj);
		}
	}
	
	public void remove(Department obj) {
		this.dao.deleteById(obj.getId());
	}
}
