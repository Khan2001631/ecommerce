import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import { useAuthStore } from '../store/useAuthStore';

const AiChat = () => {
  const { user } = useAuthStore();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [sessionId, setSessionId] = useState(null);
  
  // Voice Commerce State
  const [isListening, setIsListening] = useState(false);
  const [voiceEnabled, setVoiceEnabled] = useState(false);
  const recognitionRef = useRef(null);
  
  const chatEndRef = useRef(null);

  const scrollToBottom = () => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Initialize Speech Recognition
  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      recognitionRef.current = new SpeechRecognition();
      recognitionRef.current.continuous = false;
      recognitionRef.current.interimResults = false;
      recognitionRef.current.lang = 'en-US';

      recognitionRef.current.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        setInput(transcript);
        setIsListening(false);
        // Auto-submit the voice query
        sendMessageToApi(transcript);
      };

      recognitionRef.current.onerror = (event) => {
        console.error('Speech recognition error', event.error);
        setIsListening(false);
      };

      recognitionRef.current.onend = () => {
        setIsListening(false);
      };
    }
  }, [sessionId, user, voiceEnabled]); // Added dependencies to ensure sendMessageToApi captures latest state

  const toggleListening = () => {
    if (isListening) {
      recognitionRef.current?.stop();
    } else {
      recognitionRef.current?.start();
      setIsListening(true);
    }
  };

  const sendMessageToApi = async (messageText) => {
    if (!messageText.trim()) return;

    const userMessage = { type: 'user', content: messageText };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);
    setError('');

    try {
      const payload = { 
        question: messageText,
        user_id: user?.id,
        session_id: sessionId 
      };
      
      const response = await axios.post('http://localhost:8000/chat', payload, {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (response.data.session_id && !sessionId) {
        setSessionId(response.data.session_id);
      }

      const responseText = response.data.message || '';
      const aiMessage = {
        type: 'ai',
        content: responseText,
        products: response.data.products || []
      };
      setMessages(prev => [...prev, aiMessage]);

      // Text-to-Speech Output
      if (voiceEnabled && responseText) {
        const utterance = new SpeechSynthesisUtterance(responseText);
        window.speechSynthesis.speak(utterance);
      }

    } catch (err) {
      setError('Failed to get response from AI. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    sendMessageToApi(input);
  };

  return (
    <div className="flex flex-col h-screen bg-slate-950 text-white">
      {/* Header with Voice Toggle */}
      <div className="flex justify-between items-center p-4 bg-slate-900 border-b border-slate-700/50">
        <h2 className="text-xl font-bold bg-gradient-to-r from-orange-400 to-violet-400 bg-clip-text text-transparent">Agentic Assistant</h2>
        <button 
          onClick={() => setVoiceEnabled(!voiceEnabled)}
          className={`px-4 py-2 rounded-full text-sm font-semibold transition-colors ${voiceEnabled ? 'bg-orange-500/20 text-orange-400 border border-orange-500/50' : 'bg-slate-800 text-slate-400 border border-slate-700'}`}
        >
          {voiceEnabled ? '🔊 Voice Output: ON' : '🔈 Voice Output: OFF'}
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map((msg, index) => (
          <div key={index} className={`flex ${msg.type === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-2xl ${
              msg.type === 'user'
                ? 'bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 text-white'
                : 'bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 text-slate-300'
            }`}>
              <p>{msg.content}</p>
              {msg.products && msg.products.length > 0 && (
                <div className="mt-2">
                  <p className="font-semibold text-slate-200">Recommended Products:</p>
                  <ul className="list-disc list-inside text-slate-400">
                    {msg.products.map(product => (
                      <li key={product.id || product.name}>{product.name}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>
          </div>
        ))}
        {loading && (
          <div className="flex justify-start">
            <div className="bg-slate-900/50 backdrop-blur-xl border border-slate-700/50 px-4 py-2 rounded-2xl text-slate-300">
              <p>AI is thinking...</p>
            </div>
          </div>
        )}
        {error && (
          <div className="flex justify-center">
            <div className="bg-red-900/50 backdrop-blur-xl border border-red-700/50 px-4 py-2 rounded-2xl text-red-400">
              <p>{error}</p>
            </div>
          </div>
        )}
        <div ref={chatEndRef} />
      </div>
      
      <form onSubmit={handleSubmit} className="p-4 bg-slate-900/50 backdrop-blur-xl border-t border-slate-700/50">
        <div className="flex shadow-lg rounded-2xl">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            className="flex-1 p-4 bg-slate-800 border border-slate-600 rounded-l-2xl text-white placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-orange-500"
            placeholder="Ask something or use your voice..."
            disabled={loading}
          />
          <button
            type="button"
            onClick={toggleListening}
            className={`px-4 border-y border-slate-600 transition-colors ${isListening ? 'bg-red-500/20 text-red-400' : 'bg-slate-800 text-slate-400 hover:text-slate-200 hover:bg-slate-700'}`}
            disabled={loading}
            title="Click to speak"
          >
            {isListening ? '🛑' : '🎤'}
          </button>
          <button
            type="submit"
            className="px-8 py-4 bg-gradient-to-r from-orange-500 via-fuchsia-500 to-violet-500 text-white rounded-r-2xl hover:brightness-110 disabled:opacity-50 font-semibold"
            disabled={loading || (!input.trim() && !isListening)}
          >
            Send
          </button>
        </div>
      </form>
    </div>
  );
};

export default AiChat;