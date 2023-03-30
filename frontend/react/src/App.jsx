import {Wrap,WrapItem, Spinner, Text} from '@chakra-ui/react'
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/card.jsx";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

const App = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchCustomers=()=>{
        setLoading(true);

        getCustomers().then(res => {
            setCustomers(res.data);
        }).catch(err => {
            errorNotification(err.code,err.response.data.message);
        }).finally(() => {
            setLoading(false);
        })
    }
    useEffect(() => {
        fetchCustomers();
    }, [])
    //
    if (loading) {
        return (<SidebarWithHeader>
                <CreateCustomerDrawer ></CreateCustomerDrawer>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>

        )
    }
    if(customers.length<=0){
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers={fetchCustomers}
                ></CreateCustomerDrawer>
                <Text>无用户信息</Text>
            </SidebarWithHeader>
        )
    }
    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer fetchCustomers={fetchCustomers}></CreateCustomerDrawer>
            <Wrap justify='center' spacing='30px'>
            {
                customers.map((customer, index) => {
                    return (
                        <WrapItem key={index}>
                        <CardWithImage {...customer}
                                       fetchCustomers={fetchCustomers}
                        />
                        </WrapItem>
                    )
                })
            }
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App;