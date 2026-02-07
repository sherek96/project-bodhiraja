// File: src/components/Login.jsx
import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Box, Card, TextField, Button, Typography, Alert } from "@mui/material";

function Login() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    const handleLogin = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/user/getByUsername/${username}`);
            // Check if user exists AND password matches
            if (response.data && response.data.password === password) {
                navigate("/dashboard");
            } else {
                setErrorMsg("Invalid username or password");
            }
        } catch (error) {
            console.error(error);
            setErrorMsg("Connection to Backend failed!");
        }
    };

    return (
        <Box sx={{ 
            height: "100vh", display: "flex", justifyContent: "center", alignItems: "center", 
            background: "linear-gradient(135deg, #1976d2 30%, #21CBF3 90%)"
        }}>
            <Card sx={{ width: 400, p: 4, borderRadius: 4, boxShadow: 5 }}>
                <Box sx={{ textAlign: 'center', mb: 3 }}>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
                        Bodhiraja
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                        Management Information System
                    </Typography>
                </Box>

                {errorMsg && <Alert severity="error" sx={{ mb: 2 }}>{errorMsg}</Alert>}
                
                <TextField fullWidth label="Username" variant="filled" margin="normal" 
                    onChange={(e) => setUsername(e.target.value)} />
                
                <TextField fullWidth type="password" label="Password" variant="filled" margin="normal" 
                    onChange={(e) => setPassword(e.target.value)} />
                
                <Button fullWidth variant="contained" size="large" sx={{ mt: 3, py: 1.5, fontWeight: 'bold' }} 
                    onClick={handleLogin}>
                    Login
                </Button>
            </Card>
        </Box>
    );
}
export default Login;