import { useEffect, useState } from "react";
import axios from "axios";
import {
  Container,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
} from "@mui/material";
import Navbar from "./Navbar";
import { useNavigate } from "react-router-dom";

function StudentList() {
  const [students, setStudents] = useState([]);
  const navigate = useNavigate();

  // 1. Fetch Data
  useEffect(() => {
    // We define the function inside to keep things clean
    const loadStudents = async () => {
      try {
        // MAKE SURE this matches your Java Controller URL!
        const result = await axios.get("http://localhost:8080/student/all");
        setStudents(result.data);
      } catch (error) {
        console.error("Error loading students:", error);
      }
    };

    loadStudents();
  }, []);

  return (
    <div>
      <Navbar title="Student Management" />

      <Container maxWidth="lg">
        <Button
          variant="contained"
          sx={{ mb: 2 }}
          onClick={() => navigate("/add-student")}
        >
          + Add New Student
        </Button>
        ;
        <TableContainer component={Paper} sx={{ boxShadow: 3 }}>
          <Table>
            <TableHead sx={{ backgroundColor: "#1976d2" }}>
              <TableRow>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  ID
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Name
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Grade
                </TableCell>
                <TableCell sx={{ color: "white", fontWeight: "bold" }}>
                  Actions
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {students.map((student) => (
                <TableRow key={student.id}>
                  <TableCell>{student.id}</TableCell>
                  {/* Make sure these match your Java Entity fields! */}
                  <TableCell>{student.nameWithInitial}</TableCell>
                  <TableCell>{student.grade?.name || "N/A"}</TableCell>
                  <TableCell>
                    <Button size="small" color="primary">
                      Edit
                    </Button>
                    <Button size="small" color="error">
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Container>
    </div>
  );
}

export default StudentList;
