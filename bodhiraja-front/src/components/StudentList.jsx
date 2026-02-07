import { useEffect, useState } from "react";
import axios from "axios";
import { 
    Box, Table, TableBody, TableCell, TableContainer, 
    TableHead, TableRow, Paper, Typography, Button, Container 
} from "@mui/material";
import { useNavigate } from "react-router-dom";

function StudentList() {
    const navigate = useNavigate();
    const [students, setStudents] = useState([]);
    
    // --- THE FIX: A "Trigger" to force reloads safely ---
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    // 1. The Effect watches 'refreshTrigger'. 
    // It runs once at the start, and again whenever we update the trigger.
    useEffect(() => {
        const fetchStudents = async () => {
            try {
                const result = await axios.get("http://localhost:8080/student/all");
                setStudents(result.data);
            } catch (error) {
                console.error("Error loading students:", error);
            }
        };

        fetchStudents();
    }, [refreshTrigger]); 

    // 2. The Delete Function
    const deleteStudent = async (id) => {
        const confirmDelete = window.confirm("Are you sure you want to delete this student?");
        if (confirmDelete) {
            try {
                await axios.delete(`http://localhost:8080/student/delete/${id}`);
                
                // --- THE MAGIC: Just change the number to force a reload ---
                setRefreshTrigger(prev => prev + 1);
                
            } catch (error) {
                console.error("Error deleting student:", error);
                alert("Failed to delete student.");
            }
        }
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Box sx={{ display: "flex", justifyContent: "space-between", mb: 3 }}>
                <Typography variant="h4" component="h2" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
                    Student List
                </Typography>
                <Box>
                    <Button 
                        variant="contained" 
                        color="success" 
                        sx={{ mr: 2 }} 
                        onClick={() => navigate("/add-student")}
                    >
                        + Add New
                    </Button>
                    <Button variant="outlined" onClick={() => navigate("/dashboard")}>
                        Back to Dashboard
                    </Button>
                </Box>
            </Box>

            <TableContainer component={Paper} sx={{ boxShadow: 5, borderRadius: 2 }}>
                <Table sx={{ minWidth: 650 }} aria-label="student table">
                    <TableHead sx={{ backgroundColor: "#1976d2" }}>
                        <TableRow>
                            <TableCell sx={{ color: "white", fontWeight: "bold" }}>ID</TableCell>
                            <TableCell sx={{ color: "white", fontWeight: "bold" }}>Name</TableCell>
                            <TableCell sx={{ color: "white", fontWeight: "bold" }}>Grade</TableCell>
                            <TableCell sx={{ color: "white", fontWeight: "bold" }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {students.map((student) => (
                            <TableRow key={student.id}>
                                <TableCell>{student.id}</TableCell>
                                <TableCell>{student.nameWithInitial}</TableCell>
                                <TableCell>{student.grade?.name || "No Grade"}</TableCell>
                                <TableCell>
                                    <Button color="primary" size="small">Edit</Button>
                                    <Button 
                                        color="error" 
                                        size="small" 
                                        sx={{ ml: 1 }}
                                        onClick={() => deleteStudent(student.id)}
                                    >
                                        Delete
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Container>
    );
}

export default StudentList;