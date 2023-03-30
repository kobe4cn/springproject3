import {
    Button, CloseButton,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Input, useDisclosure
} from "@chakra-ui/react";
import CreateCustomerForm from "./CreateCustomerForm.jsx";
import UpdateCustomerForm from "./UpdateCustomerForm.jsx";

const AddIcon=()=>"+";
const CloseIcon=()=>"X";
const UpdateCustomerDrawer=({fetchCustomers,initialValues,customerId})=>{
    const { isOpen, onOpen, onClose } = useDisclosure()
    return (
        <>
        <Button mt={8} bg={"gray.200"} color={"white"} rounded={"full"} _hover={{
            transform: 'translateY(-2px)',
            boxShadow: 'lg'
        }}

                onClick={onOpen}>
            修改
        </Button>
    <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
        <DrawerOverlay />
        <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>修改</DrawerHeader>

            <DrawerBody>
                <UpdateCustomerForm fetchCustomers={fetchCustomers} initialValues={initialValues} customerId={customerId}></UpdateCustomerForm>
            </DrawerBody>

            <DrawerFooter>
                <Button leftIcon={<CloseIcon/>}
                        colorScheme={"teal"}
                        onClick={onClose}>
                    关闭
                </Button>
            </DrawerFooter>
        </DrawerContent>
    </Drawer>
        </>
    )
}

export default UpdateCustomerDrawer;


