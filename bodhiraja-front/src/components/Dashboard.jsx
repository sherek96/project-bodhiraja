// File: src/components/Dashboard.jsx
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Container,
  Box,
  Paper,
} from "@mui/material";
import { useNavigate } from "react-router-dom";

function Dashboard() {
  const navigate = useNavigate();

  return (
    <Box sx={{ flexGrow: 1 }}>
      {/* Top Navigation Bar */}
      <AppBar position="static" sx={{ backgroundColor: "#1565c0" }}>
        <Toolbar>
          <Typography
            variant="h6"
            component="div"
            sx={{ flexGrow: 1, fontWeight: "bold" }}
          >
            Bodhiraja MIS
          </Typography>
          <Button color="inherit" onClick={() => navigate("/")}>
            Logout
          </Button>
        </Toolbar>
      </AppBar>

      {/* Main Content Area */}
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Paper sx={{ p: 4, textAlign: "center", backgroundColor: "#f5f5f5" }}>
          <Typography variant="h4" gutterBottom color="primary">
            Welcome to the Dashboard
          </Typography>
          <Typography variant="body1">
            Select a module from the menu to begin managing the Pirivena.
          </Typography>

          {/* Placeholder for future buttons */}
          <Box
            sx={{ mt: 4, display: "flex", gap: 2, justifyContent: "center" }}
          >
            <Button variant="contained" color="success">
              yo yo
            </Button>
            <Button
              variant="contained"
              color="success"
              onClick={() => navigate("/students")} 
            >
              Manage Students
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
export default Dashboard;
