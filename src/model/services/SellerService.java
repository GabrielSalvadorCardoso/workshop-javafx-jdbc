package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao dao = DaoFactory.createSellerDao();

	public List<Seller> findAll() {
		List<Seller> Sellers = new ArrayList<Seller>();
		return this.dao.findAll();
	}

	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) {
			this.dao.insert(obj);
		} else {
			this.dao.update(obj);
		}
	}

	public void remove(Seller obj) {
		this.dao.deleteById(obj.getId());
	}
}
