import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import { ChakraProvider } from '@chakra-ui/react'
import './index.css'
import { createStandaloneToast } from '@chakra-ui/toast'
import {createBrowserRouter,RouterProvider} from "react-router-dom";
import Login from "./components/login/login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";


const { ToastContainer } = createStandaloneToast()
const router=createBrowserRouter([
    {
        path:"/",
        element: <Login/>
    },
    {
        path:"/dashboard",
        element:<App/>
    }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
      <ChakraProvider>
          <AuthProvider>
              <RouterProvider router={router}/>
          </AuthProvider>
          <ToastContainer/>
      </ChakraProvider>
  </React.StrictMode>,
)
