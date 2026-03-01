import { useState, useEffect } from 'react';
import { X } from 'lucide-react';

const StudentRegistration = ({ isOpen, onClose }) => {
  const [isRendered, setIsRendered] = useState(isOpen);
  const [isClosing, setIsClosing] = useState(false);

  // 1. Updated State to match your exact database fields
  const [formData, setFormData] = useState({
    indexno: '',
    fullname: '',
    name_with_initials: '',
    nic: '',
    dob: '',
    note: '',
    guardian_id: '',
    user_id: '',
    studentstatus_id: '',
    student_type_id: ''
  });

  useEffect(() => {
    if (isOpen) {
      setIsRendered(true);
      setIsClosing(false);
      setFormData({
        indexno: '', fullname: '', name_with_initials: '', nic: '', 
        dob: '', note: '', guardian_id: '', user_id: '', 
        studentstatus_id: '', student_type_id: ''
      }); 
    } else if (isRendered) {
      setIsClosing(true);
      setTimeout(() => {
        setIsRendered(false);
        setIsClosing(false);
      }, 400); 
    }
  }, [isOpen, isRendered]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevData => ({ ...prevData, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault(); 
    console.log("Data ready for backend:", formData); 
    alert("Data captured! Check your browser console.");
    onClose();
  };

  if (!isRendered && !isOpen) return null;

  // Reusable Tailwind class string for inputs to keep code clean
  const inputClass = "w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 outline-none transition-shadow";
  const labelClass = "block text-sm font-medium text-gray-700 mb-1";

  return (
    <div className={`fixed inset-0 z-50 bg-gray-900/40 backdrop-blur-sm flex justify-center items-center p-4 ${isClosing ? 'animate-glass-out' : 'animate-glass'}`}>
      {/* Increased max-w-3xl to give this larger form some breathing room */}
      <div className={`bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col overflow-hidden ${isClosing ? 'animate-modal-out' : 'animate-modal'}`}>
        
        {/* Header (Fixed) */}
        <div className="flex justify-between items-center p-6 border-b border-gray-200">
          <h2 className="text-xl font-semibold text-gray-800">Register New Student</h2>
          <button onClick={onClose} className="text-gray-500 hover:text-red-600 transition">
            <X size={24} />
          </button>
        </div>

        {/* Form Body (Scrollable if screen is too small) */}
        <div className="p-6 overflow-y-auto">
          <form onSubmit={handleSubmit} className="space-y-6">
            
            {/* Section 1: Personal Details */}
            <div className="space-y-4">
              <h3 className="text-sm font-semibold text-blue-600 uppercase tracking-wider">Personal Information</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className={labelClass}>Index Number</label>
                  <input type="text" name="indexno" value={formData.indexno} onChange={handleChange} required className={inputClass} placeholder="e.g. STU-2026-001" />
                </div>
                <div>
                  <label className={labelClass}>NIC (If applicable)</label>
                  <input type="text" name="nic" value={formData.nic} onChange={handleChange} className={inputClass} placeholder="e.g. 200012345678" />
                </div>
                <div className="md:col-span-2">
                  <label className={labelClass}>Full Name</label>
                  <input type="text" name="fullname" value={formData.fullname} onChange={handleChange} required className={inputClass} placeholder="Student's Full Name" />
                </div>
                <div>
                  <label className={labelClass}>Name with Initials</label>
                  <input type="text" name="name_with_initials" value={formData.name_with_initials} onChange={handleChange} required className={inputClass} placeholder="e.g. A. B. C. Perera" />
                </div>
                <div>
                  <label className={labelClass}>Date of Birth</label>
                  <input type="date" name="dob" value={formData.dob} onChange={handleChange} required className={inputClass} />
                </div>
              </div>
            </div>

            {/* Section 2: System & Enrollment Details */}
            <div className="space-y-4 pt-4 border-t border-gray-100">
              <h3 className="text-sm font-semibold text-blue-600 uppercase tracking-wider">Enrollment Details</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className={labelClass}>Student Type</label>
                  <select name="student_type_id" value={formData.student_type_id} onChange={handleChange} required className={inputClass}>
                    <option value="">Select Type</option>
                    <option value="1">Monk (Bhikkhu)</option>
                    <option value="2">Lay Student</option>
                  </select>
                </div>
                <div>
                  <label className={labelClass}>Status</label>
                  <select name="studentstatus_id" value={formData.studentstatus_id} onChange={handleChange} required className={inputClass}>
                    <option value="">Select Status</option>
                    <option value="1">Active</option>
                    <option value="2">Inactive / Graduated</option>
                  </select>
                </div>
                <div>
                  <label className={labelClass}>Assign Guardian</label>
                  <select name="guardian_id" value={formData.guardian_id} onChange={handleChange} className={inputClass}>
                    <option value="">Select Guardian...</option>
                    {/* These would normally be populated dynamically from your database */}
                    <option value="1">Kamal Perera (Guardian ID: 1)</option>
                    <option value="2">Saman Silva (Guardian ID: 2)</option>
                  </select>
                </div>
                <div>
                  <label className={labelClass}>Link User Account</label>
                  <select name="user_id" value={formData.user_id} onChange={handleChange} className={inputClass}>
                    <option value="">Select User Account...</option>
                    <option value="101">user_st_001</option>
                    <option value="102">user_st_002</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Section 3: Additional Notes */}
            <div className="pt-4 border-t border-gray-100">
              <label className={labelClass}>Additional Notes</label>
              <textarea 
                name="note" 
                value={formData.note} 
                onChange={handleChange} 
                rows="3" 
                className={`${inputClass} resize-none`} 
                placeholder="Any medical notes, special requirements, or enrollment details..."
              ></textarea>
            </div>

            {/* Footer Actions */}
            <div className="mt-8 flex justify-end space-x-3 pt-4 border-t border-gray-200">
              <button type="button" onClick={onClose} className="px-5 py-2.5 text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition font-medium">
                Cancel
              </button>
              <button type="submit" className="px-5 py-2.5 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition shadow-md font-medium">
                Save Student Record
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  );
};

export default StudentRegistration;