import { useState } from 'react';
import StudentTable from './StudentTable';
import StudentRegistration from './StudentRegistration';
import StudentView from './StudentView';

const StudentManagement = () => {
  // State to control what is visible
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedStudent, setSelectedStudent] = useState(null);

  return (
    <div className="flex h-[calc(100vh-8rem)] gap-4">
      
      {/* The Table Container
        Notice the transition classes: If a student is selected, it shrinks to w-2/3.
      */}
      <div className={`transition-all duration-300 ease-in-out ${selectedStudent ? 'w-2/3' : 'w-full'}`}>
        <StudentTable 
          onAddClick={() => setIsModalOpen(true)} 
          onViewClick={(student) => setSelectedStudent(student)} 
        />
      </div>

      {/* The Right Side Panel */}
      {selectedStudent && (
        <StudentView 
          student={selectedStudent} 
          onClose={() => setSelectedStudent(null)} 
        />
      )}

      {/* The Glassmorphism Form Modal */}
      <StudentRegistration 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
      />

    </div>
  );
};

export default StudentManagement;