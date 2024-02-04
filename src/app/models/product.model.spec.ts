import { Product } from './product.model';

describe('Product', () => {
  it('should create an instance', () => {
    // @ts-ignore
    expect(new Product()).toBeTruthy();
  });
});
