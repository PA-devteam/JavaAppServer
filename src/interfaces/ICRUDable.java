package interfaces;

public interface ICRUDable {
    public boolean create(Object o);
    public Object read(int id);
    public boolean update(Object o);
    public boolean delete(int id);
}
