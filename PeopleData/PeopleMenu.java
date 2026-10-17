import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {

    private JPanel mainPanel;
    private JTable personTable;
    private JLabel titleLabel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JTextField emailField;
    private JComboBox<String> pangkatComboBox;
    private JComboBox<String> statusComboBox;
    private JButton addUpdateButton;
    private JButton deleteButton;
    private JButton cancelButton;

    private Database database;
    private DefaultTableModel tableModel;
    private String selectedId = null;

    public PeopleMenu() {
        super("Manajemen Data Personel");

        database = new Database();
        
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 520);
        setLocationRelativeTo(null);

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        String[] pangkatData = {
                "Prada", "Pratu", "Praka", "Kopda", "Koptu", "Kopka",
                "Serda", "Sertu", "Serka", "Serma", "Pelda", "Peltu",
                "Letda", "Lettu", "Kapten", "Mayor", "Letkol", "Kolonel",
                "Brigjen", "Mayjen", "Letjen", "Jenderal"
        };
        pangkatComboBox.setModel(new DefaultComboBoxModel<>(pangkatData));
        statusComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Aktif Dinas", "Cuti Dinas"
        }));

        // Tabel dibentuk dari data database, tanpa ArrayList.
        String[] columns = {"ID", "Nama", "Tahun Lahir", "Pangkat", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        personTable.setModel(tableModel);
        personTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadTable();
        deleteButton.setVisible(false);

        addUpdateButton.addActionListener(e -> {
            if (selectedId == null) {
                insertData();
            } else {
                updateData();
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedId == null) {
                showError("Pilih data pada tabel yang ingin dihapus.");
                return;
            }

            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Yakin ingin menghapus data ID " + selectedId + "?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION
            );
            if (answer == JOptionPane.YES_OPTION) {
                deleteData();
            }
        });

        cancelButton.addActionListener(e -> clearForm());

        // Mengambil data dari baris tabel yang dipilih, tanpa ArrayList.
        personTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = personTable.getSelectedRow();
                if (row < 0) {
                    return;
                }

                selectedId = tableModel.getValueAt(row, 0).toString();
                idField.setText(selectedId);
                idField.setEditable(false);
                namaField.setText(tableModel.getValueAt(row, 1).toString());
                tahunLahirField.setText(tableModel.getValueAt(row, 2).toString());
                pangkatComboBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                emailField.setText(tableModel.getValueAt(row, 4).toString());
                statusComboBox.setSelectedItem(tableModel.getValueAt(row, 5).toString());

                addUpdateButton.setText("Update");
                deleteButton.setVisible(true);
            }
        });
    }

    // Validasi dipakai saat INSERT dan UPDATE.
    private boolean validateForm() {
        if (idField.getText().trim().isEmpty()
                || namaField.getText().trim().isEmpty()
                || tahunLahirField.getText().trim().isEmpty()
                || emailField.getText().trim().isEmpty()
                || pangkatComboBox.getSelectedItem() == null
                || statusComboBox.getSelectedItem() == null) {
            showError("Semua kolom wajib diisi.");
            return false;
        }

        try {
            int year = Integer.parseInt(tahunLahirField.getText().trim());
            if (year < 1950 || year > java.time.Year.now().getValue()) {
                showError("Tahun lahir harus antara 1950 dan tahun sekarang.");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Tahun lahir harus berupa angka.");
            return false;
        }

        if (!emailField.getText().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            showError("Format email tidak valid.");
            return false;
        }

        return true;
    }

    private Person getFormPerson() {
        return new Person(
                idField.getText().trim(),
                namaField.getText().trim(),
                Integer.parseInt(tahunLahirField.getText().trim()),
                pangkatComboBox.getSelectedItem().toString(),
                emailField.getText().trim(),
                statusComboBox.getSelectedItem().toString()
        );
    }

    // READ: mengikuti modul dengan database.selectQuery(sql).
    private void loadTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT id, nama, tahun_lahir, pangkat, email, status FROM person ORDER BY id";

        try (ResultSet resultSet = database.selectQuery(sql)) {
            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("id"),
                        resultSet.getString("nama"),
                        resultSet.getInt("tahun_lahir"),
                        resultSet.getString("pangkat"),
                        resultSet.getString("email"),
                        resultSet.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Gagal membaca data dari database.\n" + e.getMessage());
        } catch (RuntimeException e) {
            showError("Gagal membaca data dari database.\n" + getRootMessage(e));
        }
    }

    // CREATE: data disimpan ke database melalui insertUpdateDeleteQuery.
    private void insertData() {
        if (!validateForm()) {
            return;
        }

        Person person = getFormPerson();

        try {
            // Pemeriksaan awal agar ID yang sudah dipakai menampilkan pesan yang jelas.
            if (idExists(person.getId())) {
                showError("ID sudah digunakan. Masukkan ID yang berbeda.");
                return;
            }

            String sql = "INSERT INTO person (id, nama, tahun_lahir, pangkat, email, status) VALUES ('"
                    + escapeSql(person.getId()) + "', '"
                    + escapeSql(person.getNama()) + "', "
                    + person.getTahunLahir() + ", '"
                    + escapeSql(person.getPangkat()) + "', '"
                    + escapeSql(person.getEmail()) + "', '"
                    + escapeSql(person.getStatus()) + "')";

            database.insertUpdateDeleteQuery(sql);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan.");
        } catch (RuntimeException e) {
            if (isDuplicateKeyError(e)) {
                showError("ID sudah digunakan. Masukkan ID yang berbeda.");
            } else {
                showError("Gagal menambahkan data.\n" + getRootMessage(e));
            }
        }
    }

    // UPDATE: mengubah data berdasarkan ID dari baris yang dipilih.
    private void updateData() {
        if (selectedId == null || !validateForm()) {
            return;
        }

        Person person = getFormPerson();
        String sql = "UPDATE person SET nama='" + escapeSql(person.getNama())
                + "', tahun_lahir=" + person.getTahunLahir()
                + ", pangkat='" + escapeSql(person.getPangkat())
                + "', email='" + escapeSql(person.getEmail())
                + "', status='" + escapeSql(person.getStatus())
                + "' WHERE id='" + escapeSql(selectedId) + "'";

        try {
            database.insertUpdateDeleteQuery(sql);
            loadTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
        } catch (RuntimeException e) {
            showError("Gagal mengubah data.\n" + getRootMessage(e));
        }
    }

    // DELETE: menghapus data berdasarkan ID yang dipilih.
    private void deleteData() {
        if (selectedId == null) {
            return;
        }

        String sql = "DELETE FROM person WHERE id='" + escapeSql(selectedId) + "'";
        try {
            int deleted = database.insertUpdateDeleteQuery(sql);

            if (deleted == 0) {
                showError("Data tidak ditemukan atau sudah dihapus.");
            } else {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            }

            loadTable();
            clearForm();
        } catch (RuntimeException e) {
            showError("Gagal menghapus data.\n" + getRootMessage(e));
        }
    }

    private boolean idExists(String id) {
        String sql = "SELECT id FROM person WHERE id='" + escapeSql(id) + "'";
        try (ResultSet resultSet = database.selectQuery(sql)) {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Mengamankan tanda kutip agar input teks tidak merusak query SQL.
    private String escapeSql(String value) {
        return value.replace("\\", "\\\\").replace("'", "''");
    }

    private boolean isDuplicateKeyError(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof SQLException) {
                SQLException sqlException = (SQLException) current;
                if (sqlException.getErrorCode() == 1062) {
                    return true;
                }
                String message = sqlException.getMessage();
                if (message != null && message.toLowerCase().contains("duplicate")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

    private String getRootMessage(Throwable error) {
        Throwable current = error;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? error.toString() : current.getMessage();
    }

    private void clearForm() {
        selectedId = null;
        idField.setEditable(true);
        idField.setText("");
        namaField.setText("");
        tahunLahirField.setText("");
        emailField.setText("");
        pangkatComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        personTable.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Abaikan jika look and feel sistem tidak tersedia.
            }

            try {
                new PeopleMenu().setVisible(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(
                        null,
                        e.getMessage(),
                        "Koneksi Database Gagal",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
