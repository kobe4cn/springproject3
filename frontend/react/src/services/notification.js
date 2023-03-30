import { createStandaloneToast } from '@chakra-ui/toast'

const {  toast } = createStandaloneToast()

const notification=(title,description,status)=>{
    toast({
        title,
        description,
        status,
        duration: 4000,
        isClosable: true,
    })
}

export const successNotification=(title,desc)=>{
    notification(title,desc,"success");
}

export const errorNotification=(title,desc)=>{
    notification(title,desc,"error");
}
