package WalleServer.persistence;

import WalleServer.http.exceptions.DuplicateKeyException;
import WalleServer.http.exceptions.NotFoundException;
import WalleServer.utils.CRUD;

import java.util.Optional;
import java.util.stream.IntStream;

public class Database implements CRUD<Person> {

    private final JSONArrayList<Person> data;

    public Database() {
        data = new JSONArrayList<>();
        data.add(new Person("Oswaldo", "Vazquez", 25, "ASDASDASDA"));
        data.add(new Person("Carlos", "Herrera", 23, "asdlasdjkasljd"));
    }

    @Override
    public Optional<Person> get(String curp) {
        return data.stream()
                .filter(record -> curp.equals(record.getCurp()))
                .findAny();
    }

    @Override
    public JSONArrayList<Person> getAll() {
        return data;
    }

    @Override
    public Person create(Person person) throws DuplicateKeyException {
        var optionalRecord = data.stream()
                .filter(record -> person.getCurp().equals(record.getCurp()))
                .findAny();

        if (optionalRecord.isPresent()) {
            var message = String.format("Person with CURP %s already exists.", optionalRecord.get().getCurp());
            throw new DuplicateKeyException(message, 404);
        }

        data.add(person);
        return data.get(data.size() - 1);
    }

    @Override
    public Person update(Person person) throws NotFoundException {
        var indexToUpdate = IntStream.range(0, data.size())
                .filter(index -> person.getCurp().equals(data.get(index).getCurp()))
                .findFirst();

        if (indexToUpdate.isEmpty()) {
            var message = String.format("Person with CURP %s doesn't exists.", person.getCurp());
            throw new NotFoundException(message);
        }

        data.set(indexToUpdate.getAsInt(), person);
        return data.get(indexToUpdate.getAsInt());
    }

    @Override
    public void delete(String curp) {
        data.removeIf(person -> curp.equals(person.getCurp()));
    }

}
