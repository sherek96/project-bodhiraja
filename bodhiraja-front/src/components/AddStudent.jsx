import { useState } from "react";
import axios from "axios";
import {
  Container,
  TextField,
  Button,
  Paper,
  Typography,
  Box,
  MenuItem,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";

function AddStudent() {
  const navigate = useNavigate();



  // 1. State for the REQUIRED fields
  const [formData, setFormData] = useState({
    indexNo: "",
    fullname: "",
    nameWithInitial: "",
    nic: "",
    dob: "", // Date of Birth
    type: "Scholarship", // Default value
    note: "",
  });

  // Handle typing in any box
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Send the data to Java
      // Java will add the Grade, Guardian, etc. automatically!
      await axios.post("http://localhost:8080/student/add", formData);

      alert("Student Added Successfully!");
      navigate("/students");
    } catch (error) {
      console.error("Error adding student:", error);
      alert("Failed to add student. Check console.");
    }
  };

  return (
    <div>
      <Navbar title="Add New Student" />
      <Container maxWidth="md">
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography
            variant="h5"
            sx={{ mb: 3, fontWeight: "bold", color: "#1976d2" }}
          >
            New Student Registration
          </Typography>

          <form onSubmit={handleSubmit}>
            {/* Index Number (Must be Unique!) */}
            <TextField
              fullWidth
              label="Index No (e.g., ST001)"
              name="indexNo"
              onChange={handleChange}
              required
              margin="normal"
            />

            {/* Names */}
            <TextField
              fullWidth
              label="Full Name"
              name="fullname"
              onChange={handleChange}
              required
              margin="normal"
            />
            <TextField
              fullWidth
              label="Name with Initials"
              name="nameWithInitial"
              onChange={handleChange}
              required
              margin="normal"
            />

            {/* NIC (Optional) */}
            <TextField
              fullWidth
              label="NIC (Optional)"
              name="nic"
              onChange={handleChange}
              margin="normal"
            />

            {/* Date of Birth */}
            <TextField
              fullWidth
              label="Date of Birth"
              name="dob"
              type="date"
              InputLabelProps={{ shrink: true }} // Keeps the label up
              onChange={handleChange}
              required
              margin="normal"
            />

            {/* Type Dropdown */}
            <TextField
              fullWidth
              select
              label="Student Type"
              name="type"
              value={formData.type}
              onChange={handleChange}
              margin="normal"
            >
              <MenuItem value="Scholarship">Scholarship</MenuItem>
              <MenuItem value="Paying">Paying</MenuItem>
            </TextField>

            {/* Note */}
            <TextField
              fullWidth
              label="Note"
              name="note"
              multiline
              rows={2}
              onChange={handleChange}
              margin="normal"
            />

            <Box sx={{ mt: 3, display: "flex", gap: 2 }}>
              <Button type="submit" variant="contained" size="large">
                Save Student
              </Button>
              <Button
                variant="outlined"
                color="error"
                onClick={() => navigate("/students")}
              >
                Cancel
              </Button>
            </Box>
          </form>
        </Paper>
      </Container>
    </div>
  );
}

export default AddStudent;
