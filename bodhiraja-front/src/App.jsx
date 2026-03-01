import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import Dashboard from './pages/Dashboard';
import Administration from './pages/Administration';
import StudentManagement from './pages/student/StudentManagement';

function App() {
  return (
    <BrowserRouter>
      <div className="flex h-screen bg-white">
        
        <Sidebar />
        
        <div className="flex-1 flex flex-col overflow-hidden">
          <Navbar />
          
          {/* PAGE CONTENT: This is where the magic happens */}
          <main className="flex-1 overflow-x-hidden overflow-y-auto bg-sky-100 p-6 rounded-lg">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/student" element={<StudentManagement/>} />
              <Route path="/administration" element={<Administration />} />
            </Routes>
          </main>

        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;