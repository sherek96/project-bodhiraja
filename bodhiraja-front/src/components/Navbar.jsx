import { AppBar, Toolbar, Typography, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

function Navbar({ title }) {
    const navigate = useNavigate();

    return (
        <AppBar position="static" sx={{ mb: 4 }}> {/* mb: 4 adds margin at bottom */}
            <Toolbar>
                {/* The Title changes based on which page uses this Navbar */}
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    Bodhiraja MIS - {title}
                </Typography>

                <Button color="inherit" onClick={() => navigate("/dashboard")}>
                    Home
                </Button>
                <Button color="inherit" onClick={() => navigate("/")}>
                    Logout
                </Button>
            </Toolbar>
        </AppBar>
    );
}

export default Navbar;