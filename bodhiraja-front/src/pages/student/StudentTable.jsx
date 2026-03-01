import { Eye, Plus } from 'lucide-react';

const StudentTable = ({ onAddClick, onViewClick }) => {
  // Dummy data to test the layout
  const students = [
    { id: 'STU-001', name: 'Saman Kumara', grade: 'Grade 10', date: '2026-01-15' },
    { id: 'STU-002', name: 'Nimal Perera', grade: 'Grade 11', date: '2026-01-18' },
  ];

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden h-full flex flex-col">
      <div className="p-4 border-b border-gray-200 flex justify-between items-center bg-gray-50">
        <h2 className="font-semibold text-gray-800">Enrolled Students</h2>
        <button 
          onClick={onAddClick}
          className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition shadow-sm text-sm"
        >
          <Plus size={16} /> Add Student
        </button>
      </div>

      <div className="overflow-x-auto flex-1">
        <table className="w-full text-left text-sm text-gray-600">
          <thead className="bg-gray-50 text-gray-700 uppercase font-medium border-b border-gray-200">
            <tr>
              <th className="px-6 py-3">Reg ID</th>
              <th className="px-6 py-3">Name</th>
              <th className="px-6 py-3">Class</th>
              <th className="px-6 py-3">Enrolled</th>
              <th className="px-6 py-3 text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            {students.map((student) => (
              <tr key={student.id} className="border-b border-gray-100 hover:bg-gray-50 transition">
                <td className="px-6 py-4 font-medium text-gray-900">{student.id}</td>
                <td className="px-6 py-4">{student.name}</td>
                <td className="px-6 py-4">{student.grade}</td>
                <td className="px-6 py-4">{student.date}</td>
                <td className="px-6 py-4 text-right">
                  <button 
                    onClick={() => onViewClick(student)}
                    className="text-blue-600 hover:text-blue-800 p-1.5 rounded hover:bg-blue-50 transition"
                  >
                    <Eye size={18} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default StudentTable;