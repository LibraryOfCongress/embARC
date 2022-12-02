package com.portalmedia.embarc.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dizitart.no2.FindOptions;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.exceptions.NitriteException;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

/**
 * Service class for nitrite database
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DBService<T> {

	ObjectRepository<T> repo;

	public DBService(Class<T> typeParameterClass) {
		repo = NitriteConnection.getRepository(typeParameterClass);
	}

	@SuppressWarnings("unchecked")
	public Boolean add(T value) {
		boolean result = false;
		try {
			repo.insert(value);
			result = true;
		} catch (final NitriteException ex) {
			System.out.println("DBService.add failed with exception: " + ex);
			return false;
		} catch (final Exception ex) {
			System.out.println("DBService.add failed with exception: " + ex);
			return false;
		}
		return result;
	}

	public void closeDB() {
		System.out.println("DBService.closeDB");
		try {
			if (!repo.isDropped()) {
				repo.close();
			}
		} catch (final NitriteException ex) {
			System.out.println("DBService.close failed with exception: " + ex);
		}
	}

	public Boolean delete(int id) {
		boolean result = false;
		try {
			repo.remove(ObjectFilters.eq("id", id));
			result = true;
		} catch (final NitriteException ex) {
			System.out.println("DBService.delete failed with exception: " + ex);
			return false;
		}
		return result;
	}

	public Boolean deleteAll() {
		boolean result = false;
		try {
			repo.drop();
			repo = NitriteConnection.getRepository(repo.getType());
			result = true;

		} catch (final NitriteException ex) {
			System.out.println("DBService.deleteAll failed with exception: " + ex);
			return false;
		}
		return result;
	}

	public void dropCollection() {
		try {
			if (!repo.isDropped() && !repo.isClosed()) {
				repo.drop();
			}
		} catch (final NitriteException ex) {
			System.out.println("DBService.drop failed with exception: " + ex);
		}
	}

	public T get(int id) {
		T item = null;
		try {
			item = repo.find(ObjectFilters.eq("id", id)).firstOrDefault();
		} catch (final NitriteException ex) {
			System.out.println("DBService.get failed with exception: " + ex);
		}
		return item;
	}

	public List<T> get(int offset, int size, String sortCol, boolean sortAsc) {
		System.out.println(sortCol + ":" + sortAsc);
		final SortOrder sortOrder = sortAsc ? SortOrder.Ascending : SortOrder.Descending;
		final List<T> itemList = new ArrayList<>();
		try {
			FindOptions.sort(sortCol, sortOrder);
			final Cursor<T> results = repo.find(FindOptions.limit(offset, size));
			for (final T item : results) {
				itemList.add(item);
			}
		} catch (final NitriteException ex) {
			System.out.println("DBService.getAll failed with exception: " + ex);
		}
		return itemList;
	}

	public List<T> getAll() {
		final List<T> itemList = new ArrayList<>();
		try {
			final Cursor<T> results = repo.find();
			for (final T item : results) {
				itemList.add(item);
			}
		} catch (final NitriteException ex) {
			System.out.println("DBService.getAll failed with exception: " + ex);
		}
		return itemList;
	}

	public Cursor<T> getAllCursors() {
		try {
			return repo.find();
		} catch (final NitriteException ex) {
			System.out.println("DBService.getAll failed with exception: " + ex);
		}
		return null;
	}

	public Cursor<T> getEditedCursors() {
		try {
			return repo.find(ObjectFilters.eq("edited", true));
		} catch (final NitriteException ex) {
			System.out.println("DBService.getAll failed with exception: " + ex);
		}
		return null;
	}

	public int getErrorCount(Set<ValidationRuleSetEnum> rules) {

		if (rules != null && rules.size() > 0) {
			final List<ObjectFilter> filters = new ArrayList<>();
			for (final ValidationRuleSetEnum r : rules) {
				if (r == ValidationRuleSetEnum.SMPTE_C) {
					filters.add(ObjectFilters.eq("smptecError", true));
				} else if (r == ValidationRuleSetEnum.FADGI_O) {
					filters.add(ObjectFilters.eq("fadgioError", true));
				} else if (r == ValidationRuleSetEnum.FADGI_R) {
					filters.add(ObjectFilters.eq("fadgirError", true));
				} else if (r == ValidationRuleSetEnum.FADGI_SR) {
					filters.add(ObjectFilters.eq("fadgisrError", true));
				}
			}
			ObjectFilter f = filters.get(0);
			for (int i = 1; i < filters.size(); i++) {
				f = ObjectFilters.or(f, filters.get(i));
			}
			try {
				final Cursor<T> results = repo.find(f);
				return results.totalCount();
			} catch (final NitriteException ex) {
				System.out.println("DBService.getAll failed with exception: " + ex);
			}
		}

		return 0;
	}

	public List<T> getErrors(int offset, int size, Set<ValidationRuleSetEnum> rules, String sortCol, boolean sortAsc) {

		System.out.println(sortCol + ":" + sortAsc);
		final SortOrder sortOrder = sortAsc ? SortOrder.Ascending : SortOrder.Descending;
		final List<T> itemList = new ArrayList<>();
		if (rules.size() > 0) {
			final List<ObjectFilter> filters = new ArrayList<>();
			for (final ValidationRuleSetEnum r : rules) {
				if (r == ValidationRuleSetEnum.SMPTE_C) {
					filters.add(ObjectFilters.eq("smptecError", true));
				} else if (r == ValidationRuleSetEnum.FADGI_O) {
					filters.add(ObjectFilters.eq("fadgioError", true));
				} else if (r == ValidationRuleSetEnum.FADGI_R) {
					filters.add(ObjectFilters.eq("fadgirError", true));
				} else if (r == ValidationRuleSetEnum.FADGI_SR) {
					filters.add(ObjectFilters.eq("fadgisrError", true));
				}
			}
			ObjectFilter f = filters.get(0);
			for (int i = 1; i < filters.size(); i++) {
				f = ObjectFilters.or(f, filters.get(i));
			}
			try {
				FindOptions.sort(sortCol, sortOrder);
				final Cursor<T> results = repo.find(f, FindOptions.limit(offset, size));
				for (final T item : results) {
					itemList.add(item);
				}
			} catch (final NitriteException ex) {
				System.out.println("DBService.getAll failed with exception: " + ex);
			}
		}

		return itemList;
	}

	public long getSize() {
		System.out.println("Repo closed: " + repo.isClosed());
		System.out.println("Repo name: " + repo.getName());
		System.out.println("Repo name: " + repo.getType());

		return repo.size();
	}

	public void update(T file) {
		try {
			repo.update(file);
		} catch (final NitriteException ex) {
			System.out.println("DBService.update failed with exception: " + ex);
		}
	}

}
