import { X, User } from 'lucide-react';

const StudentView = ({ student, onClose }) => {
  if (!student) return null;

  return (
    <div className="w-1/3 bg-white border border-gray-200 rounded-lg shadow-sm h-full overflow-y-auto animate-in slide-in-from-right duration-300">
      <div className="p-4 border-b border-gray-200 flex justify-between items-center bg-gray-50">
        <h2 className="font-semibold text-gray-800">Student Details</h2>
        <button onClick={onClose} className="text-gray-500 hover:text-red-600 transition p-1 rounded hover:bg-red-50">
          <X size={20} />
        </button>
      </div>
      
      <div className="p-6 flex flex-col items-center border-b border-gray-200">
        <div className="w-20 h-20 bg-blue-50 rounded-full flex items-center justify-center text-blue-600 mb-3">
          <User size={36} />
        </div>
        <h3 className="text-lg font-bold text-gray-800">{student.name}</h3>
        <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium mt-2">Active Student</span>
      </div>

      <div className="p-6 space-y-4">
        <div>
          <p className="text-xs text-gray-500 uppercase font-semibold tracking-wider">Registration ID</p>
          <p className="font-medium text-gray-800 mt-1">{student.id}</p>
        </div>
        <div>
          <p className="text-xs text-gray-500 uppercase font-semibold tracking-wider">Current Class</p>
          <p className="font-medium text-gray-800 mt-1">{student.grade}</p>
        </div>
        <div>
          <p className="text-xs text-gray-500 uppercase font-semibold tracking-wider">Attendance Rate</p>
          <p className="font-medium text-gray-800 mt-1">96%</p>
        </div>
      </div>
    </div>
  );
};

export default StudentView;