import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { type ChangeEvent, Component, createRef } from 'react'

interface FilePickerProps {
  onFileChange: (fileList: Array<File>) => void
  placeholder: string
  clearButtonLabel?: string | undefined
  hideClearButton?: boolean | undefined
  multipleFiles?: boolean | undefined
  accept?: string | undefined
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  inputProps?: any
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  inputGroupProps?: any
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
    const { placeholder, clearButtonLabel, hideClearButton, multipleFiles, accept, inputProps } = this.props

    return (
      <div className="relative flex w-full items-center">
        <input
          type="file"
          ref={this.inputRef}
          accept={accept}
          style={{ display: 'none' }}
          multiple={multipleFiles}
          onChange={this.handleOnFileChange}
        />
        <Input
          placeholder={placeholder}
          {...{
            ...inputProps,
            readOnly: true,
            value: this.state.fileName,
            onClick: this.handleOnInputClick
          }}
          className="pr-20"
        />
        {!hideClearButton && (
          <div className="absolute right-1 top-1/2 -translate-y-1/2">
            <Button type="button" size="sm" variant="secondary" onClick={this.handleOnClearClick}>
              {clearButtonLabel ?? 'Clear'}
            </Button>
          </div>
        )}
      </div>
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
