import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Box, Card, CardContent, TextField, Button, Typography, Alert } from "@mui/material";

function Login() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    const handleLogin = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/user/getByUsername/${username}`);
            if (response.data && response.data.password === password) {
                navigate("/dashboard");
            } else {
                setErrorMsg("Invalid credentials");
            }
        } catch (err) {
            setErrorMsg("Backend connection failed");
        }
    };

    return (
        <Box sx={{ height: "100vh", display: "flex", justifyContent: "center", alignItems: "center", bgcolor: "#f5f5f5" }}>
            <Card sx={{ width: 350, p: 2 }}>
                <Typography variant="h5" sx={{ mb: 2, textAlign: 'center' }}>Login</Typography>
                {errorMsg && <Alert severity="error" sx={{ mb: 2 }}>{errorMsg}</Alert>}
                <TextField fullWidth label="Username" margin="normal" onChange={(e) => setUsername(e.target.value)} />
                <TextField fullWidth type="password" label="Password" margin="normal" onChange={(e) => setPassword(e.target.value)} />
                <Button fullWidth variant="contained" sx={{ mt: 2 }} onClick={handleLogin}>Login</Button>
            </Card>
        </Box>
    );
}
export default Login;