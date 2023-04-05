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

const AddIcon=()=>"+";
const CloseIcon=()=>"X";
const CreateCustomerDrawer=({fetchCustomers})=>{
    const { isOpen, onOpen, onClose } = useDisclosure()
    return (
        <>
        <Button leftIcon={<AddIcon/>}
                colorScheme={"teal"}
                onClick={onOpen}>
            创建用户
        </Button>
    <Drawer isOpen={isOpen} onClose={onClose} size={"xl"}>
        <DrawerOverlay />
        <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>创建用户</DrawerHeader>

            <DrawerBody>
                <CreateCustomerForm onSuccess={fetchCustomers}></CreateCustomerForm>
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

export default CreateCustomerDrawer;


