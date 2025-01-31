import { useState, useEffect, useRef } from "react";
import axios from "axios";
import "bootstrap/dist/css/bootstrap.min.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBars, faTimes } from '@fortawesome/free-solid-svg-icons';
import { Paper, Button, TextField } from '@mui/material';

const ChatComponent = () => {
  const [input, setInput] = useState("");
  const [sessionId, setSessionId] = useState(() => localStorage.getItem("sessionId") || generateSessionId());
  const [chatHistory, setChatHistory] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuRef = useRef(null);

  // Generate a unique session ID
  const generateSessionId = () => {
    return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
  };

  // Fetch chat sessions
  const fetchChatSessions = async () => {
    setLoading(true);
    try {
      const res = await axios.get("http://localhost:8080/api/chat/sessions");
      setSessions(res.data);
    } catch (error) {
      console.error("Error fetching chat sessions:", error);
    } finally {
      setLoading(false);
    }
  };

  // Fetch chat history for a specific session
  const fetchChatHistory = async (id) => {
    setLoading(true);
    try {
      const res = await axios.get(`http://localhost:8080/api/chat/history/${id}`);
      setChatHistory(res.data);
    } catch (error) {
      console.error("Error fetching chat history:", error);
    } finally {
      setLoading(false);
    }
  };

  // Handle sending a chat message
  const handleChat = async () => {
    if (!input.trim()) return;

    setLoading(true);
    try {
      const res = await axios.post(`http://localhost:8080/api/chat/${sessionId}/${encodeURIComponent(input)}`);
      setChatHistory(res.data);
      setInput("");
      fetchChatSessions();
    } catch (error) {
      console.error("Error sending message:", error);
    } finally {
      setLoading(false);
    }
  };

  // Clear chat history for the current session
  const clearHistory = async () => {
    setLoading(true);
    try {
      await axios.delete(`http://localhost:8080/api/chat/history/${sessionId}`);
      setChatHistory([]);
      fetchChatSessions();
    } catch (error) {
      console.error("Error clearing history:", error);
    } finally {
      setLoading(false);
    }
  };

  // Start a new chat session
  const newChat = () => {
    const newSessionId = generateSessionId();
    setSessionId(newSessionId);
    setChatHistory([]);
    fetchChatHistory(newSessionId);
  };

  // Toggle the menu
  const toggleMenu = () => {
    setIsMenuOpen((prev) => !prev);
  };

  // Close the menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  // Fetch chat sessions and history when sessionId changes
  useEffect(() => {
    localStorage.setItem("sessionId", sessionId);
    fetchChatSessions();
    fetchChatHistory(sessionId);
  }, [sessionId]);

  return (
    <div className="container-fluid">
      {/* Navbar with offcanvas */}
      <nav className="navbar bg-body-tertiary fixed-top">
        <div className="container-fluid">
          <button
            className="navbar-toggler"
            type="button"
            onClick={toggleMenu}
            aria-controls="offcanvasNavbar"
            aria-expanded={isMenuOpen ? "true" : "false"}
            aria-label="Toggle navigation"
          >
            <FontAwesomeIcon icon={isMenuOpen ? faTimes : faBars} />
          </button>
          <div
            ref={menuRef}
            className={`offcanvas offcanvas-end ${isMenuOpen ? "show" : ""}`}
            tabIndex="-1"
            id="offcanvasNavbar"
            aria-labelledby="offcanvasNavbarLabel"
          >
            <div className="offcanvas-header">
              <h5 className="offcanvas-title" id="offcanvasNavbarLabel">Chat Sessions</h5>
              <button
                type="button"
                className="btn-close"
                onClick={toggleMenu}
                aria-label="Close"
              ></button>
            </div>
            <div className="offcanvas-body">
              <ul className="navbar-nav justify-content-end flex-grow-1 pe-3">
                <li className="nav-item">
                  <button className="nav-link btn" onClick={newChat}>New Chat</button>
                </li>
                <li className="nav-item">
                  <button className="nav-link btn" onClick={clearHistory}>Clear Chat History</button>
                </li>
                <div className="list-group">
                  {sessions.map((session) => (
                    <div
                      key={session}
                      className={`list-group-item ${session === sessionId ? "active" : ""}`}
                      onClick={() => setSessionId(session)}
                      style={{ cursor: "pointer" }}
                    >
                      {session}
                    </div>
                  ))}
                </div>
              </ul>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="pt-5">
        <div className="d-flex flex-column align-items-center">
          <h2 className="text-center mb-4">LIKESTEM AI Chat</h2>
          {loading && <p>Loading...</p>}

          {/* Chat Container */}
          <Paper
            elevation={3}
            className="chat-container p-3 mb-4"
            style={{
              maxHeight: "60vh",
              overflowY: "auto",
              width: "90%", // Responsive width
              maxWidth: "800px", // Limit width on larger screens
            }}
          >
            <strong>Current Conversation:</strong>
            {chatHistory.length > 0 ? (
              chatHistory.map((message, index) => (
                <p key={index} style={{ wordBreak: "break-word" }}>{message}</p>
              ))
            ) : (
              <p>No messages yet.</p>
            )}
          </Paper>

          {/* Input and Send Button */}
          <div
            className="d-flex flex-column w-100 align-items-center"
            style={{ width: "90%", maxWidth: "800px" }} // Responsive width
          >
            <TextField
              label="Enter your message"
              multiline
              rows={3}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              variant="outlined"
              fullWidth
              className="mb-2" // Spacing
              disabled={loading}
            />
            <Button
              variant="contained"
              color="primary"
              onClick={handleChat}
              disabled={loading}
              style={{ width: "100%" }} // Full width on smaller screens
            >
              Send
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatComponent;
