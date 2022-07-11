import { InputGroup, InputRightAddon } from '@chakra-ui/react'
import { HasChildren } from '../../../util/react-types.util'

type InputProps = {
  suffix: string | undefined
} & HasChildren

export const InputWithAddon = ({ suffix, children }: InputProps) => {
  if (suffix) {
    return (
      <InputGroup>
        {children}
        <InputRightAddon children={suffix} />
      </InputGroup>
    )
  }
  return <>{children}</>
}
