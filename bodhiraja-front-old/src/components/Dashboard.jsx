import { Typography, Container, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

function Dashboard() {
    const navigate = useNavigate();
    return (
        <Container sx={{ mt: 5 }}>
            <Typography variant="h3">Welcome to Dashboard</Typography>
            <Button variant="outlined" sx={{ mt: 2 }} onClick={() => navigate("/")}>Logout</Button>
        </Container>
    );
}
export default Dashboard;