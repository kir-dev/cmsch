// from https://github.com/PedroDBFlores/chakra-ui-file-picker

import { Button, Input, InputGroup, InputGroupProps, InputRightElement } from '@chakra-ui/react'
import { ChangeEvent, Component, createRef, FC } from 'react'

interface FilePickerProps {
  onFileChange: (fileList: Array<File>) => void
  placeholder: string
  clearButtonLabel?: string | undefined
  hideClearButton?: boolean | undefined
  multipleFiles?: boolean | undefined
  accept?: string | undefined
  inputProps?: InputGroupProps | undefined
  inputGroupProps?: InputGroupProps | undefined
}

interface FilePickerState {
  files: FileList | null
  fileName: string
}

export class FilePicker extends Component<FilePickerProps, FilePickerState> {
  static defaultProps = {
    clearButtonLabel: 'Clear',
    multipleFiles: false,
    accept: undefined,
    hideClearButton: false,
    inputProps: undefined,
    inputGroupProps: undefined
  }

  private inputRef = createRef<HTMLInputElement>()

  constructor(props: FilePickerProps) {
    super(props)
    this.state = {
      files: null,
      fileName: ''
    }
  }

  componentDidUpdate(_: FilePickerProps, prevState: FilePickerState): void {
    if (prevState.files !== this.state.files) {
      const fileArray = new Array<File>()
      if (this.state.files) {
        for (const file of this.state.files) {
          fileArray.push(file)
        }
      }
      this.setState({ ...this.state, fileName: fileArray.map((f) => f.name).join(' & ') })
      this.props.onFileChange(fileArray)
    }
  }

  public reset = (): void => this.handleOnClearClick()

  render = () => {
    const { placeholder, clearButtonLabel, hideClearButton, multipleFiles, accept, inputProps, inputGroupProps } = this.props

    return (
      <InputGroup {...inputGroupProps}>
        <input
          type="file"
          ref={this.inputRef}
          accept={accept}
          style={{ display: 'none' }}
          multiple={multipleFiles}
          onChange={this.handleOnFileChange}
          data-testid={inputProps?._placeholder ?? placeholder}
        />
        <Input
          placeholder={placeholder}
          {...{
            ...inputProps,
            readOnly: true,
            isReadOnly: true,
            value: this.state.fileName,
            onClick: this.handleOnInputClick
          }}
        />
        {!hideClearButton && <ClearButton clearButtonLabel={clearButtonLabel ?? 'Clear'} onButtonClick={this.handleOnClearClick} />}
      </InputGroup>
    )
  }

  private handleOnFileChange = (ev: ChangeEvent<HTMLInputElement>) => {
    this.setState({ ...this.state, files: ev.target.files })
    this.clearInnerInput()
  }

  private handleOnClearClick = () => {
    this.setState({ ...this.state, files: null })
    this.clearInnerInput()
  }

  private clearInnerInput() {
    if (this.inputRef?.current) {
      this.inputRef.current.files = null
    }
  }

  private handleOnInputClick = () => {
    if (this.inputRef?.current) {
      this.inputRef.current.value = ''
      this.inputRef.current.click()
    }
  }
}

type ClearButtonProps = Pick<FilePickerProps, 'clearButtonLabel'> & {
  onButtonClick: () => void
}

// eslint-disable-next-line react-refresh/only-export-components
const ClearButton: FC<ClearButtonProps> = ({ clearButtonLabel, onButtonClick }) => (
  <InputRightElement width="4.5rem">
    <Button onClick={onButtonClick}>{clearButtonLabel ?? 'Clear'}</Button>
  </InputRightElement>
)
