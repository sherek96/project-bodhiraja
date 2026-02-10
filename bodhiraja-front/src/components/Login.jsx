// 1. Imports: We need tools from Material UI to make it look good
import { Box, Card, Typography, TextField, Button } from "@mui/material";
import { useNavigate } from "react-router-dom"; // Hook for navigation
import { useState } from "react"; // Hook for managing state (like form inputs)

// 2. The Function: In React, a UI piece is just a JavaScript function
function Login() {
    const navigate = useNavigate();

    // create variable to hold what user type
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = () => {
        if (username === 'admin' && password === '1234') {
            navigate("/dashboard");
        } else {
            alert('Wrong user name or password')
        }

    }
    
    // 3. The Return: This looks like HTML, but it's actually JavaScript (JSX)
    return (
        <Box sx={{ 
            height: "100vh", display: "flex", justifyContent: "center", alignItems: "center", 
            backgroundColor: "#e3f2fd" 
        }}>
            <Card sx={{ padding: 4, width: 350, boxShadow: 5 }}>
                <Typography variant="h5" sx={{ mb: 2, textAlign: "center", fontWeight: "bold" }}>
                    Bodhiraja Login
                </Typography>

                <TextField 
                    fullWidth label="Username" margin="normal" 
                    onChange={(e) => setUsername(e.target.value)} // Capture typing
                />
                
                <TextField 
                    fullWidth type="password" label="Password" margin="normal" 
                    onChange={(e) => setPassword(e.target.value)} // Capture typing
                />

                <Button 
                    fullWidth variant="contained" sx={{ mt: 2 }}
                    onClick={handleLogin} // 5. Run function on click
                >
                    Sign In
                </Button>
            </Card>
        </Box>
    );
}

// 4. Export: Allow other files (like App.jsx) to use this component
export default Login;