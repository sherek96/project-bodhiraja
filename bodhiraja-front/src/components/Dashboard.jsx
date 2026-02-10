import { Container, Grid, Card, CardContent, Typography, CardActionArea } from "@mui/material";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar"; // Import our new reusable header!

function Dashboard() {
    const navigate = useNavigate();

    // This is our data for the menu cards
    const modules = [
        { title: "Manage Students", path: "/students", color: "#2196f3" }, // Blue
        { title: "Manage Staff", path: "/staff", color: "#4caf50" },       // Green
        { title: "Attendance", path: "/attendance", color: "#ff9800" },    // Orange
    ];

    return (
        <div>
            {/* 1. The Top Bar */}
            <Navbar title="Dashboard" />

            {/* 2. The Main Content Area */}
            <Container maxWidth="lg">
                <Typography variant="h4" sx={{ mb: 4, fontWeight: 'bold', color: '#555' }}>
                    Welcome, Administrator
                </Typography>

                {/* 3. The Grid System */}
                <Grid container spacing={4}>
                    {modules.map((mod) => (
                        <Grid item xs={12} sm={6} md={4} key={mod.title}>
                            <Card sx={{ height: 150, backgroundColor: mod.color, color: 'white' }}>
                                {/* CardActionArea makes the whole card clickable */}
                                <CardActionArea 
                                    sx={{ height: "100%" }} 
                                    onClick={() => navigate(mod.path)}
                                >
                                    <CardContent sx={{ textAlign: 'center', mt: 2 }}>
                                        <Typography variant="h5" component="div" fontWeight="bold">
                                            {mod.title}
                                        </Typography>
                                    </CardContent>
                                </CardActionArea>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            </Container>
        </div>
    );
}

export default Dashboard;