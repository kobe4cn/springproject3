import {
    createContext,
useContext,
useEffect,
useState
} from "react";
import jwt_decode from "jwt-decode";
import {useRadioGroupContext} from "@chakra-ui/react";
import {getCustomers, getCustomersByEmail, login as performlogin} from "../../services/client.js";

const AuthContext=createContext({})

const AuthProvider=({children})=>{
    const [customer,setCustomer]=useState(null);
    useEffect(()=>{
        let token=localStorage.getItem("access_token");
        if(token){
            token=jwt_decode(token);
            getCustomersByEmail(token.sub).then(res=>{
                //console.log("in");
                setCustomer({
                    name:token.sub,
                    roles: token.scopes
                })
                //console.log(res);
            }).catch(err=>{
                console.log(err);
            })
            //getCustomers()

        }
    },[])
    const login=async (usernameAndPassword) =>{
        return new Promise((resolve, reject)=>{
            performlogin(usernameAndPassword).then(res=>{
                const jwtToken=res.headers["authorization"];
                console.log(jwtToken);
                setCustomer({
                    ...res.data.customerDTO
                })
                resolve(res);
            }).catch(err=>{
                reject(err);
            })
        })
    }

    const logout=()=>{
        localStorage.removeItem("access_token");
        setCustomer(null);
    }
    const isCustomerAuthenticated=()=>{
        const token=localStorage.getItem("access_token");
        if(!token){
            return false;
        }
        const decoded=jwt_decode(token);
        if(Date.now()>decoded.exp*1000){
            logout()
            return false;
        }
        return true;


    }
    return <AuthContext.Provider value={{
        customer,login,
        logout,isCustomerAuthenticated
    }
    }>
        {children}
    </AuthContext.Provider>
}
export const useAuth=()=> useContext(AuthContext);
export default AuthProvider;